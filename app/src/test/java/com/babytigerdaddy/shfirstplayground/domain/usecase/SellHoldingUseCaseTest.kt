package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SellHoldingUseCaseTest {

    private class FakeHoldingRepo : HoldingRepository {
        val map = LinkedHashMap<String, Holding>()
        override fun observeAll(): Flow<List<Holding>> = flowOf(map.values.toList())
        override suspend fun getById(id: String) = map[id]
        override suspend fun save(holding: Holding) { map[holding.id] = holding }
        override suspend fun delete(id: String) { map.remove(id) }
    }

    private class FakeJournalRepo : TradeJournalRepository {
        val map = LinkedHashMap<LocalDate, TradeJournalEntry>()
        override fun observeAll(): Flow<List<TradeJournalEntry>> = flowOf(map.values.toList())
        override suspend fun getByDate(date: LocalDate) = map[date]
        override suspend fun save(entry: TradeJournalEntry) { map[entry.date] = entry }
        override suspend fun delete(date: LocalDate) { map.remove(date) }
    }

    private fun holding(ticker: String, buy: Long, current: Long, qty: Int): Holding {
        val d = LocalDate.parse("2026-06-01")
        return Holding(
            id = ticker,
            ticker = ticker,
            buyPrice = buy,
            currentPrice = current,
            quantity = qty,
            entryDate = d,
            createdAt = LocalDateTime.of(d, LocalTime.NOON),
        )
    }

    @Test
    fun `매도하면 실현손익이 그날 일지에 남고 보유에서 빠진다`() = runBlocking {
        val holdings = FakeHoldingRepo()
        val journal = FakeJournalRepo()
        val h = holding("삼성전기", buy = 100, current = 300, qty = 10) // 평가손익 +2,000
        holdings.save(h)
        val sell = SellHoldingUseCase(holdings, journal)
        val day = LocalDate.parse("2026-06-30")

        sell(h, mood = TradeMood.SATISFIED, note = "55일 보유 익절", soldOn = day)

        // 보유에서 제거
        assertNull(holdings.getById("삼성전기"))
        // 일지에 실현손익 + 종목 + 기분/메모
        val entry = journal.getByDate(day)!!
        assertEquals(2_000L, entry.realizedPnl)
        assertTrue(entry.tickers.contains("삼성전기"))
        assertEquals(TradeMood.SATISFIED, entry.mood)
        assertEquals("55일 보유 익절", entry.note)
    }

    @Test
    fun `같은 날 두 종목 매도하면 손익 합산되고 기분은 기존 유지`() = runBlocking {
        val holdings = FakeHoldingRepo()
        val journal = FakeJournalRepo()
        val a = holding("A", buy = 100, current = 200, qty = 10) // +1,000
        val b = holding("B", buy = 100, current = 50, qty = 10)  // -500
        holdings.save(a); holdings.save(b)
        val sell = SellHoldingUseCase(holdings, journal)
        val day = LocalDate.parse("2026-06-30")

        sell(a, mood = TradeMood.DISCIPLINED, note = "첫 매도", soldOn = day)
        sell(b, mood = TradeMood.REGRET, note = "둘째 매도", soldOn = day)

        val entry = journal.getByDate(day)!!
        assertEquals(500L, entry.realizedPnl) // 1,000 - 500
        assertTrue(entry.tickers.containsAll(listOf("A", "B")))
        // 두 번째 매도의 기분/메모는 무시, 첫 기록 유지
        assertEquals(TradeMood.DISCIPLINED, entry.mood)
        assertEquals("첫 매도", entry.note)
    }
}
