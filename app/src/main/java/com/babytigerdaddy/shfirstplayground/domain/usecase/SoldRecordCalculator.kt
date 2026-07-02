package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.RealizedPoint
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import java.time.DayOfWeek

/** 누적 실현손익을 끊어 볼 단위. */
enum class PnlPeriod { WEEKLY, MONTHLY }

/** 기간(주/월) 한 구간의 실현손익과 그때까지의 누적. */
data class PeriodPnlPoint(val label: String, val realizedPnl: Long, val cumulative: Long)

/**
 * 매도 내역 → 집계([SoldHistorySummary]) 순수 변환기.
 */
object SoldRecordCalculator {

    /**
     * 매도 내역을 주간/월간으로 묶어 각 구간 실현손익 + 누적을 반환.
     * 판 내역 누적 그래프를 주/월 단위로 끊어 보여줄 때 쓴다(빈 구간은 건너뜀).
     */
    fun periodPoints(records: List<SoldRecord>, period: PnlPeriod): List<PeriodPnlPoint> {
        if (records.isEmpty()) return emptyList()
        val grouped = records.groupBy { r ->
            when (period) {
                PnlPeriod.WEEKLY -> r.soldDate.with(DayOfWeek.MONDAY) // 그 주 월요일
                PnlPeriod.MONTHLY -> r.soldDate.withDayOfMonth(1)     // 그 달 1일
            }
        }.toSortedMap()
        var running = 0L
        return grouped.map { (start, recs) ->
            val sum = recs.sumOf { it.realizedPnl }
            running += sum
            val label = when (period) {
                PnlPeriod.WEEKLY -> "%d.%d 주".format(start.monthValue, start.dayOfMonth)
                PnlPeriod.MONTHLY -> "%d월".format(start.monthValue)
            }
            PeriodPnlPoint(label = label, realizedPnl = sum, cumulative = running)
        }
    }

    fun compute(records: List<SoldRecord>): SoldHistorySummary {
        if (records.isEmpty()) return SoldHistorySummary.EMPTY

        val byDate = records.sortedBy { it.soldDate }

        var running = 0L
        val cumulative = byDate.map { r ->
            running += r.realizedPnl
            RealizedPoint(soldDate = r.soldDate, realizedPnl = r.realizedPnl, cumulative = running)
        }

        val saleCount = records.size
        val winCount = records.count { it.isWin }
        val winRate = winCount.toDouble() / saleCount
        val avgReturnRate = records.sumOf { it.returnRate } / saleCount

        return SoldHistorySummary(
            totalRealized = running,
            saleCount = saleCount,
            winCount = winCount,
            winRate = winRate,
            avgReturnRate = avgReturnRate,
            cumulative = cumulative,
            bestSale = records.maxByOrNull { it.realizedPnl }?.takeIf { it.realizedPnl > 0 },
            records = byDate.asReversed(), // 최근 매도 우선
        )
    }
}
