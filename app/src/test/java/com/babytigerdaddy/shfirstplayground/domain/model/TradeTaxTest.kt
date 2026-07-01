package com.babytigerdaddy.shfirstplayground.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TradeTaxTest {

    @Test
    fun `총 제비용 = 매수수수료 + 매도수수료 + 매도거래세`() {
        // 매수 100,000 / 매도 200,000
        // 매수수수료 15 + 매도수수료 30 + 매도거래세 400 = 445
        assertEquals(445L, TradeTax.totalCost(buyAmount = 100_000, sellAmount = 200_000))
    }

    @Test
    fun `보유 세후 평가손익은 세전에서 제비용 뺀 값`() {
        val h = Holding(
            id = "x", ticker = "A", buyPrice = 1_000, currentPrice = 2_000, quantity = 100,
            entryDate = LocalDate.parse("2026-06-01"),
            createdAt = LocalDateTime.of(LocalDate.parse("2026-06-01"), LocalTime.NOON),
        )
        assertEquals(100_000L, h.evalPnl)       // 세전
        assertEquals(445L, h.estimatedFee)
        assertEquals(99_555L, h.netEvalPnl)     // 세후
    }

    @Test
    fun `매도 세후 실현손익도 제비용 반영`() {
        val r = SoldRecord(
            id = "y", ticker = "A", buyPrice = 1_000, sellPrice = 2_000, quantity = 100,
            entryDate = LocalDate.parse("2026-06-01"), soldDate = LocalDate.parse("2026-06-10"),
            createdAt = LocalDateTime.of(LocalDate.parse("2026-06-10"), LocalTime.NOON),
        )
        assertEquals(100_000L, r.realizedPnl)   // 세전
        assertEquals(445L, r.fee)
        assertEquals(99_555L, r.netRealizedPnl) // 세후
    }
}
