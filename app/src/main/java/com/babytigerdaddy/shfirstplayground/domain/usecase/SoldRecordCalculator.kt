package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.RealizedPoint
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord

/**
 * 매도 내역 → 집계([SoldHistorySummary]) 순수 변환기.
 */
object SoldRecordCalculator {

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
