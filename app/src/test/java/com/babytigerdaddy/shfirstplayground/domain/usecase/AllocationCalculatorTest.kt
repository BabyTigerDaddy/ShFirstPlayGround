package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AllocationCalculatorTest {

    private fun holding(ticker: String, current: Long, qty: Int): Holding {
        val d = LocalDate.parse("2026-06-01")
        return Holding(
            id = "$ticker-$current-$qty", ticker = ticker,
            buyPrice = current, currentPrice = current, quantity = qty,
            entryDate = d, createdAt = LocalDateTime.of(d, LocalTime.NOON),
        )
    }

    @Test
    fun `빈 리스트는 EMPTY`() {
        assertSame(AssetAllocation.EMPTY, AllocationCalculator.compute(emptyList()))
    }

    @Test
    fun `비중은 종목 평가금액 나누기 총평가, 내림차순, 집중 종목`() {
        val a = AllocationCalculator.compute(
            listOf(
                holding("A", current = 100, qty = 60),  // 평가 6,000
                holding("B", current = 100, qty = 30),  // 평가 3,000
                holding("C", current = 100, qty = 10),  // 평가 1,000
            ),
        )
        assertEquals(10_000L, a.totalEval)
        assertEquals("A", a.slices[0].ticker)
        assertEquals(0.6, a.slices[0].ratio, 1e-9)
        assertEquals(0.3, a.slices[1].ratio, 1e-9)
        assertEquals(0.1, a.slices[2].ratio, 1e-9)
        // 집중 종목 = 제일 큰 비중
        assertEquals("A", a.concentrationTicker)
        assertEquals(0.6, a.concentrationRatio, 1e-9)
    }

    @Test
    fun `같은 종목 여러 건은 합산`() {
        val a = AllocationCalculator.compute(
            listOf(
                holding("삼성전기", current = 100, qty = 30), // 3,000
                holding("삼성전기", current = 100, qty = 20), // 2,000 → 합 5,000
                holding("카카오", current = 100, qty = 50),   // 5,000
            ),
        )
        assertEquals(2, a.slices.size) // 종목 2개로 합쳐짐
        assertEquals(10_000L, a.totalEval)
        val samsung = a.slices.first { it.ticker == "삼성전기" }
        assertEquals(5_000L, samsung.evalAmount)
        assertEquals(0.5, samsung.ratio, 1e-9)
    }
}
