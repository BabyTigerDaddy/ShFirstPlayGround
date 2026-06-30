package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class RecordSaleUseCaseTest {

    private class FakeHoldingRepo : HoldingRepository {
        val map = LinkedHashMap<String, Holding>()
        override fun observeAll(): Flow<List<Holding>> = flowOf(map.values.toList())
        override suspend fun getById(id: String) = map[id]
        override suspend fun save(holding: Holding) { map[holding.id] = holding }
        override suspend fun delete(id: String) { map.remove(id) }
    }

    private class FakeSoldRepo : SoldRecordRepository {
        val map = LinkedHashMap<String, SoldRecord>()
        override fun observeAll(): Flow<List<SoldRecord>> = flowOf(map.values.toList())
        override suspend fun save(record: SoldRecord) { map[record.id] = record }
        override suspend fun delete(id: String) { map.remove(id) }
    }

    @Test
    fun `매도하면 현재가로 매도내역 남고 보유에서 빠진다`() = runBlocking {
        val holdings = FakeHoldingRepo()
        val sold = FakeSoldRepo()
        val h = Holding(
            id = "삼성전기", ticker = "삼성전기",
            buyPrice = 514_000, currentPrice = 2_196_000, quantity = 1,
            entryDate = LocalDate.parse("2026-04-08"),
            createdAt = LocalDateTime.of(LocalDate.parse("2026-04-08"), LocalTime.NOON),
        )
        holdings.save(h)
        val useCase = RecordSaleUseCase(holdings, sold)

        useCase(h, soldOn = LocalDate.parse("2026-06-30"))

        // 보유에서 제거
        assertNull(holdings.getById("삼성전기"))
        // 매도 내역에 현재가가 매도가로
        val rec = sold.map["삼성전기"]!!
        assertEquals(2_196_000L, rec.sellPrice)
        assertEquals(514_000L, rec.buyPrice)
        assertEquals(1_682_000L, rec.realizedPnl)
        assertEquals(LocalDate.parse("2026-06-30"), rec.soldDate)
        // 보유일 = 04/08 ~ 06/30
        assertEquals(83L, rec.heldDays)
    }
}
