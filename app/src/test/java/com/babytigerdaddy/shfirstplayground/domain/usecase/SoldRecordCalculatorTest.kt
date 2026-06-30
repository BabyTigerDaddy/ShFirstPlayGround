package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SoldRecordCalculatorTest {

    private fun sold(ticker: String, buy: Long, sell: Long, qty: Int, soldDate: String): SoldRecord {
        val entry = LocalDate.parse("2026-06-01")
        val d = LocalDate.parse(soldDate)
        return SoldRecord(
            id = ticker, ticker = ticker, buyPrice = buy, sellPrice = sell, quantity = qty,
            entryDate = entry, soldDate = d, createdAt = LocalDateTime.of(d, LocalTime.NOON),
        )
    }

    @Test
    fun `빈 리스트는 EMPTY`() {
        assertSame(SoldHistorySummary.EMPTY, SoldRecordCalculator.compute(emptyList()))
    }

    @Test
    fun `총 실현·승률·평균 수익률·누적·베스트`() {
        val s = SoldRecordCalculator.compute(
            listOf(
                sold("A", buy = 1_000, sell = 1_500, qty = 10, soldDate = "2026-06-10"), // +5,000 / +50%
                sold("B", buy = 1_000, sell = 800, qty = 10, soldDate = "2026-06-20"),  // -2,000 / -20%
            ),
        )
        assertEquals(3_000L, s.totalRealized)
        assertEquals(2, s.saleCount)
        assertEquals(1, s.winCount)
        assertEquals(0.5, s.winRate, 1e-9)
        // 평균 수익률 (0.5 + -0.2)/2 = 0.15
        assertEquals(0.15, s.avgReturnRate, 1e-9)
        // 누적: 매도일순 5,000 → 3,000
        assertEquals(listOf(5_000L, 3_000L), s.cumulative.map { it.cumulative })
        assertEquals("A", s.bestSale?.ticker)
        // 최근 매도 우선
        assertEquals("B", s.records.first().ticker)
    }

    @Test
    fun `손실만 있으면 베스트는 null`() {
        val s = SoldRecordCalculator.compute(
            listOf(sold("L", buy = 1_000, sell = 900, qty = 1, soldDate = "2026-06-10")),
        )
        assertEquals(null, s.bestSale)
        assertEquals(0.0, s.winRate, 1e-9)
    }
}
