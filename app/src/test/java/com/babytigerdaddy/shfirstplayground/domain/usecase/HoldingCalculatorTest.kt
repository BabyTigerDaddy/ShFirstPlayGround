package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.HoldingSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class HoldingCalculatorTest {

    private fun holding(ticker: String, buy: Long, current: Long, qty: Int = 1): Holding {
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
    fun `빈 리스트는 EMPTY`() {
        assertSame(HoldingSummary.EMPTY, HoldingCalculator.compute(emptyList()))
    }

    @Test
    fun `총 매수·평가·손익·수익률 집계`() {
        val s = HoldingCalculator.compute(
            listOf(
                holding("A", buy = 1_000, current = 1_500, qty = 10),  // 비용 10,000 / 평가 15,000 / +5,000
                holding("B", buy = 2_000, current = 1_000, qty = 5),   // 비용 10,000 / 평가 5,000 / -5,000
            ),
        )
        assertEquals(20_000L, s.totalCost)
        assertEquals(20_000L, s.totalEval)
        assertEquals(0L, s.totalPnl)
        assertEquals(0.0, s.totalReturnRate, 1e-9)
    }

    @Test
    fun `평가손익 내림차순 정렬`() {
        val s = HoldingCalculator.compute(
            listOf(
                holding("loser", buy = 1_000, current = 500, qty = 1),  // -500
                holding("winner", buy = 1_000, current = 3_000, qty = 1), // +2,000
            ),
        )
        assertEquals("winner", s.holdings.first().ticker)
        assertEquals("loser", s.holdings.last().ticker)
    }

    @Test
    fun `개별 수익률·평가손익 계산`() {
        val h = holding("삼성전기", buy = 514_000, current = 2_196_000, qty = 1)
        // (2,196,000 - 514,000) / 514,000 ≈ 3.272
        assertTrue(h.returnRate > 3.2 && h.returnRate < 3.3)
        assertEquals(1_682_000L, h.evalPnl)
        // 보유일은 거래일 기준 — 정확히 1주 뒤면 주말 빼고 5거래일(요일 무관)
        assertEquals(5L, h.holdingDays(h.entryDate.plusWeeks(1)))
    }
}
