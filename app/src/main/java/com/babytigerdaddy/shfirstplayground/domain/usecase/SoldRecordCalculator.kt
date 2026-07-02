package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.RealizedPoint
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import java.time.DayOfWeek
import java.time.LocalDate

/** 판내역 실현손익 요약 — 스와이프 카드 [전체/이번주/이번달]용. 각 기간 실현손익 합 + 매도 건수. */
data class RealizedByPeriod(
    val allRealized: Long, val allCount: Int,
    val weekRealized: Long, val weekCount: Int,
    val monthRealized: Long, val monthCount: Int,
)

/**
 * 매도 내역 → 집계([SoldHistorySummary]) 순수 변환기.
 */
object SoldRecordCalculator {

    /**
     * 실현손익을 전체 / 이번주(이번 주 월요일~오늘) / 이번달(이번 달 1일~오늘)로 묶어
     * 각 기간의 실현손익 합 + 매도 건수를 반환. 판내역 상단 스와이프 카드용.
     */
    fun realizedByPeriod(records: List<SoldRecord>, asOf: LocalDate): RealizedByPeriod {
        val weekStart = asOf.with(DayOfWeek.MONDAY)
        val monthStart = asOf.withDayOfMonth(1)
        fun inRange(from: LocalDate) =
            records.filter { !it.soldDate.isBefore(from) && !it.soldDate.isAfter(asOf) }
        val week = inRange(weekStart)
        val month = inRange(monthStart)
        return RealizedByPeriod(
            allRealized = records.sumOf { it.realizedPnl }, allCount = records.size,
            weekRealized = week.sumOf { it.realizedPnl }, weekCount = week.size,
            monthRealized = month.sumOf { it.realizedPnl }, monthCount = month.size,
        )
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
