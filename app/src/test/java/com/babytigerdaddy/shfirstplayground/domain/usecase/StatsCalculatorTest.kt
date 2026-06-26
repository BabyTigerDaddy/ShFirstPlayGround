package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.model.TradeStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class StatsCalculatorTest {

    private fun entry(
        date: String,
        pnl: Long,
        tickers: List<String> = emptyList(),
        mood: TradeMood = TradeMood.FLAT,
    ): TradeJournalEntry {
        val d = LocalDate.parse(date)
        return TradeJournalEntry(
            date = d,
            realizedPnl = pnl,
            mood = mood,
            tickers = tickers,
            createdAt = LocalDateTime.of(d, LocalTime.NOON),
            updatedAt = LocalDateTime.of(d, LocalTime.NOON),
        )
    }

    @Test
    fun `빈 리스트는 EMPTY`() {
        assertSame(TradeStats.EMPTY, StatsCalculator.compute(emptyList()))
    }

    @Test
    fun `요일별 승률은 7개 고정이고 매매 요일만 분모`() {
        // 2026-06-01 = 월요일
        val stats = StatsCalculator.compute(
            listOf(
                entry("2026-06-01", 10_000),  // 월 win
                entry("2026-06-08", -5_000),  // 월 loss
                entry("2026-06-02", 3_000),   // 화 win
            ),
        )
        assertEquals(7, stats.weekday.size)
        val mon = stats.weekday.first { it.dayOfWeek == DayOfWeek.MONDAY }
        assertEquals(2, mon.tradedDays)
        assertEquals(1, mon.winDays)
        assertEquals(0.5, mon.winRate, 1e-9)
        val sun = stats.weekday.first { it.dayOfWeek == DayOfWeek.SUNDAY }
        assertEquals(0, sun.tradedDays)
        assertEquals(0.0, sun.winRate, 1e-9)
    }

    @Test
    fun `종목별 손익은 등장한 날 손익 합산 후 내림차순`() {
        val stats = StatsCalculator.compute(
            listOf(
                entry("2026-06-01", 100_000, listOf("삼성전자")),
                entry("2026-06-02", -30_000, listOf("삼성전자", "카카오")),
                entry("2026-06-03", 50_000, listOf("카카오")),
            ),
        )
        // 삼성전자: 100,000 + (-30,000) = 70,000 / 2일
        // 카카오: (-30,000) + 50,000 = 20,000 / 2일
        assertEquals("삼성전자", stats.tickers[0].ticker)
        assertEquals(70_000L, stats.tickers[0].totalPnl)
        assertEquals(2, stats.tickers[0].tradedDays)
        assertEquals("카카오", stats.tickers[1].ticker)
        assertEquals(20_000L, stats.tickers[1].totalPnl)
    }

    @Test
    fun `평균 손익은 기록일 기준`() {
        val stats = StatsCalculator.compute(
            listOf(
                entry("2026-06-01", 30_000),
                entry("2026-06-02", -10_000),
                entry("2026-06-03", 0),
            ),
        )
        // (30000 - 10000 + 0) / 3 = 6666
        assertEquals(6_666L, stats.avgDailyPnl)
    }

    @Test
    fun `최대 연속 손실 일수`() {
        val stats = StatsCalculator.compute(
            listOf(
                entry("2026-06-01", -1_000),
                entry("2026-06-02", -2_000),
                entry("2026-06-03", 5_000),   // 끊김
                entry("2026-06-04", -1_000),
                entry("2026-06-05", -1_000),
                entry("2026-06-06", -1_000),  // 3연속
            ),
        )
        assertEquals(3, stats.maxLossStreak)
    }

    @Test
    fun `기분별 손익은 평균 손해 큰 기분이 위로`() {
        val stats = StatsCalculator.compute(
            listOf(
                entry("2026-06-01", -80_000, mood = TradeMood.OVERTRADED),
                entry("2026-06-02", -40_000, mood = TradeMood.OVERTRADED),
                entry("2026-06-03", 50_000, mood = TradeMood.DISCIPLINED),
            ),
        )
        // OVERTRADED 평균 -60,000 / DISCIPLINED 평균 +50,000 → 손해 큰 OVERTRADED가 첫 번째
        assertEquals(TradeMood.OVERTRADED, stats.mood[0].mood)
        assertEquals(-60_000L, stats.mood[0].avgPnl)
        assertEquals(2, stats.mood[0].days)
        assertEquals(-120_000L, stats.mood[0].totalPnl)
        assertEquals(TradeMood.DISCIPLINED, stats.mood[1].mood)
        assertEquals(50_000L, stats.mood[1].avgPnl)
    }

    @Test
    fun `최대 낙폭은 누적 고점 대비 최대 하락`() {
        // 누적: 100,000(고점) → 100,000 → 40,000 → 90,000 → 30,000
        // 고점 100,000은 안 깨짐 → 마지막 30,000과의 낙폭 70,000이 최대
        val stats = StatsCalculator.compute(
            listOf(
                entry("2026-06-01", 100_000),
                entry("2026-06-02", 0),
                entry("2026-06-03", -60_000),  // 낙폭 60,000
                entry("2026-06-04", 50_000),
                entry("2026-06-05", -60_000),  // 낙폭 70,000 (최대)
            ),
        )
        assertEquals(70_000L, stats.maxDrawdown)
    }
}
