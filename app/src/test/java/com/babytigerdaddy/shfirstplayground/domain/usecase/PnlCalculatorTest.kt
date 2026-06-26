package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.PnlSummary
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 추이 집계 핵심 로직 검증. 순수 함수라 Android 의존성 없이 JVM에서 바로 돈다.
 */
class PnlCalculatorTest {

    private fun entry(date: String, pnl: Long, note: String = ""): TradeJournalEntry {
        val d = LocalDate.parse(date)
        return TradeJournalEntry(
            date = d,
            realizedPnl = pnl,
            note = note,
            mood = TradeMood.FLAT,
            createdAt = LocalDateTime.of(d, java.time.LocalTime.NOON),
            updatedAt = LocalDateTime.of(d, java.time.LocalTime.NOON),
        )
    }

    @Test
    fun `빈 리스트는 EMPTY 요약을 반환`() {
        assertSame(PnlSummary.EMPTY, PnlCalculator.compute(emptyList()))
    }

    @Test
    fun `누적 손익은 입력 순서와 무관하게 날짜순으로 합산`() {
        val summary = PnlCalculator.compute(
            listOf(
                entry("2026-06-03", -50_000),
                entry("2026-06-01", 100_000),
                entry("2026-06-02", 30_000),
            ),
        )

        assertEquals(80_000L, summary.totalRealized)
        // daily는 날짜 오름차순 + 누적 단조 계산
        assertEquals(
            listOf(100_000L, 130_000L, 80_000L),
            summary.daily.map { it.cumulative },
        )
        assertEquals(LocalDate.parse("2026-06-01"), summary.daily.first().date)
    }

    @Test
    fun `승률은 매매한 날만 분모로 하고 무매매 0원은 제외`() {
        val summary = PnlCalculator.compute(
            listOf(
                entry("2026-06-01", 10_000),   // win
                entry("2026-06-02", -5_000),   // loss
                entry("2026-06-03", 0),        // 무매매 — 분모 제외
                entry("2026-06-04", 20_000),   // win
            ),
        )

        assertEquals(4, summary.recordedDays)
        assertEquals(2, summary.winDays)
        assertEquals(1, summary.lossDays)
        // 분모 = win+loss = 3, win = 2 → 2/3
        assertEquals(2.0 / 3.0, summary.winRate, 1e-9)
    }

    @Test
    fun `best와 worst는 이익날 최대 손실날 최소를 집어내고 그날 메모를 함께 담음`() {
        val summary = PnlCalculator.compute(
            listOf(
                entry("2026-06-01", 10_000),
                entry("2026-06-02", 70_000, note = "삼성전자 단타 대박"),
                entry("2026-06-03", -40_000, note = "손절 늦음"),
            ),
        )

        assertEquals(70_000L, summary.bestDay?.realizedPnl)
        assertEquals("삼성전자 단타 대박", summary.bestDay?.note)
        assertEquals(-40_000L, summary.worstDay?.realizedPnl)
        assertEquals("손절 늦음", summary.worstDay?.note)
    }

    @Test
    fun `이익만 있으면 worstDay는 null`() {
        val summary = PnlCalculator.compute(
            listOf(entry("2026-06-01", 10_000), entry("2026-06-02", 20_000)),
        )
        assertNull(summary.worstDay)
        assertEquals(20_000L, summary.bestDay?.realizedPnl)
    }

    @Test
    fun `현재 연속 흐름은 최근 날부터 같은 부호가 이어진 일수`() {
        // 최근 2일 연속 이익 → +2
        val win = PnlCalculator.compute(
            listOf(
                entry("2026-06-01", -10_000),
                entry("2026-06-02", 5_000),
                entry("2026-06-03", 8_000),
            ),
        )
        assertEquals(2, win.currentStreak)

        // 최근 날이 손실 1일 → -1
        val loss = PnlCalculator.compute(
            listOf(entry("2026-06-01", 5_000), entry("2026-06-02", -3_000)),
        )
        assertEquals(-1, loss.currentStreak)

        // 최근 날이 무매매 0원 → 0
        val flat = PnlCalculator.compute(
            listOf(entry("2026-06-01", 5_000), entry("2026-06-02", 0)),
        )
        assertEquals(0, flat.currentStreak)
    }

    @Test
    fun `월별 합계는 달별로 묶이고 yearMonth 오름차순`() {
        val summary = PnlCalculator.compute(
            listOf(
                entry("2026-05-30", 10_000),
                entry("2026-06-01", 20_000),
                entry("2026-06-15", -5_000),
            ),
        )

        assertEquals(2, summary.monthly.size)
        assertEquals("2026-05", summary.monthly[0].yearMonth)
        assertEquals(10_000L, summary.monthly[0].total)
        assertEquals("2026-06", summary.monthly[1].yearMonth)
        assertEquals(15_000L, summary.monthly[1].total)
        assertEquals(2, summary.monthly[1].recordedDays)
        assertTrue(summary.monthly[1].winDays == 1)
    }
}
