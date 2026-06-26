package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.DailyPnlPoint
import com.babytigerdaddy.shfirstplayground.domain.model.MonthlyPnl
import com.babytigerdaddy.shfirstplayground.domain.model.PnlSummary
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry

/**
 * 매매일지 엔트리 리스트 → 추이 집계([PnlSummary]) 순수 변환기.
 *
 * 의도적으로 의존성 없는 순수 함수 object로 둠 — 단위 테스트가 쉽고,
 * UI/Repository와 분리. 추이 화면 숫자는 전부 여기서 나옴.
 */
object PnlCalculator {

    /**
     * 일지들을 굴려 누적·승률·월별·연속 streak를 한 번에 계산.
     *
     * @param entries 순서 무관(내부에서 date 오름차순 정렬).
     */
    fun compute(entries: List<TradeJournalEntry>): PnlSummary {
        if (entries.isEmpty()) return PnlSummary.EMPTY

        val sorted = entries.sortedBy { it.date }

        // 일별 누적선
        var running = 0L
        val daily = sorted.map { e ->
            running += e.realizedPnl
            DailyPnlPoint(
                date = e.date,
                realizedPnl = e.realizedPnl,
                cumulative = running,
                note = e.note,
            )
        }

        val totalRealized = running
        val recordedDays = sorted.size
        val winDays = sorted.count { it.isWin }
        val lossDays = sorted.count { it.isLoss }
        // 승률은 매매한 날(이익+손실)만 분모로. 무매매 0원 날은 제외.
        val tradedDays = winDays + lossDays
        val winRate = if (tradedDays == 0) 0.0 else winDays.toDouble() / tradedDays

        val bestDay = daily.maxByOrNull { it.realizedPnl }?.takeIf { it.realizedPnl > 0 }
        val worstDay = daily.minByOrNull { it.realizedPnl }?.takeIf { it.realizedPnl < 0 }

        return PnlSummary(
            totalRealized = totalRealized,
            recordedDays = recordedDays,
            winDays = winDays,
            lossDays = lossDays,
            winRate = winRate,
            bestDay = bestDay,
            worstDay = worstDay,
            currentStreak = currentStreak(sorted),
            daily = daily,
            monthly = monthly(sorted),
        )
    }

    /**
     * 현재 연속 흐름 — 최근 날부터 거슬러 같은 부호가 이어지는 일수.
     * 이익 연속이면 +N, 손실 연속이면 -N. 가장 최근이 무매매(0)면 0.
     */
    private fun currentStreak(sortedAsc: List<TradeJournalEntry>): Int {
        val recentFirst = sortedAsc.asReversed()
        val head = recentFirst.firstOrNull() ?: return 0
        val sign = when {
            head.isWin -> 1
            head.isLoss -> -1
            else -> return 0
        }
        var count = 0
        for (e in recentFirst) {
            val matches = (sign > 0 && e.isWin) || (sign < 0 && e.isLoss)
            if (matches) count++ else break
        }
        return count * sign
    }

    /** 월별 합계(yearMonth 오름차순). */
    private fun monthly(sortedAsc: List<TradeJournalEntry>): List<MonthlyPnl> =
        sortedAsc
            .groupBy { "%04d-%02d".format(it.date.year, it.date.monthValue) }
            .toSortedMap()
            .map { (ym, list) ->
                MonthlyPnl(
                    yearMonth = ym,
                    total = list.sumOf { it.realizedPnl },
                    recordedDays = list.size,
                    winDays = list.count { it.isWin },
                )
            }
}
