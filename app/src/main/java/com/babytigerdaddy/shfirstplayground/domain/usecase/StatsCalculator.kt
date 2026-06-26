package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.MoodStat
import com.babytigerdaddy.shfirstplayground.domain.model.TickerStat
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeStats
import com.babytigerdaddy.shfirstplayground.domain.model.WeekdayStat
import java.time.DayOfWeek

/**
 * 매매일지 → 통계([TradeStats]) 순수 변환기. 의존성 없는 object라 단위 테스트 용이.
 */
object StatsCalculator {

    fun compute(entries: List<TradeJournalEntry>): TradeStats {
        if (entries.isEmpty()) return TradeStats.EMPTY

        val sorted = entries.sortedBy { it.date }

        return TradeStats(
            weekday = weekdayStats(sorted),
            tickers = tickerStats(sorted),
            avgDailyPnl = sorted.sumOf { it.realizedPnl } / sorted.size,
            maxLossStreak = maxLossStreak(sorted),
            maxDrawdown = maxDrawdown(sorted),
            mood = moodStats(sorted),
        )
    }

    /** 기분별 평균·합 손익. 기록 있는 기분만, 평균 손익 오름차순(가장 손해 본 기분이 위). */
    private fun moodStats(entries: List<TradeJournalEntry>): List<MoodStat> =
        entries.groupBy { it.mood }
            .map { (mood, list) ->
                val total = list.sumOf { it.realizedPnl }
                MoodStat(
                    mood = mood,
                    days = list.size,
                    avgPnl = total / list.size,
                    totalPnl = total,
                )
            }
            .sortedBy { it.avgPnl }

    /** 월~일 고정 7개. 매매 없는 요일도 0으로 포함. */
    private fun weekdayStats(entries: List<TradeJournalEntry>): List<WeekdayStat> {
        val byDay = entries.groupBy { it.date.dayOfWeek }
        return DayOfWeek.entries.map { day ->
            val list = byDay[day].orEmpty()
            val traded = list.count { it.isWin || it.isLoss }
            val wins = list.count { it.isWin }
            WeekdayStat(
                dayOfWeek = day,
                tradedDays = traded,
                winDays = wins,
                winRate = if (traded == 0) 0.0 else wins.toDouble() / traded,
            )
        }
    }

    /** 종목별 연관 손익(여러 종목인 날은 각자에 그날 손익 계상 — 근사). totalPnl 내림차순. */
    private fun tickerStats(entries: List<TradeJournalEntry>): List<TickerStat> {
        val acc = LinkedHashMap<String, Pair<Int, Long>>() // ticker -> (days, pnl)
        entries.forEach { e ->
            e.tickers.map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { t ->
                val (days, pnl) = acc[t] ?: (0 to 0L)
                acc[t] = (days + 1) to (pnl + e.realizedPnl)
            }
        }
        return acc.map { (t, v) -> TickerStat(ticker = t, tradedDays = v.first, totalPnl = v.second) }
            .sortedByDescending { it.totalPnl }
    }

    /** 가장 길었던 연속 손실 일수. */
    private fun maxLossStreak(sortedAsc: List<TradeJournalEntry>): Int {
        var max = 0
        var run = 0
        for (e in sortedAsc) {
            if (e.isLoss) {
                run++
                if (run > max) max = run
            } else {
                run = 0
            }
        }
        return max
    }

    /** 누적 곡선 고점 대비 최대 하락폭(원, 0 이상). */
    private fun maxDrawdown(sortedAsc: List<TradeJournalEntry>): Long {
        var cum = 0L
        var peak = 0L
        var maxDd = 0L
        for (e in sortedAsc) {
            cum += e.realizedPnl
            if (cum > peak) peak = cum
            val dd = peak - cum
            if (dd > maxDd) maxDd = dd
        }
        return maxDd
    }
}
