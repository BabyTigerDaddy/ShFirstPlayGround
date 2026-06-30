package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.HoldingSummary

/**
 * 보유 종목 리스트 → 포트폴리오 집계([HoldingSummary]) 순수 변환기.
 */
object HoldingCalculator {

    fun compute(holdings: List<Holding>): HoldingSummary {
        if (holdings.isEmpty()) return HoldingSummary.EMPTY

        val totalCost = holdings.sumOf { it.costAmount }
        val totalEval = holdings.sumOf { it.evalAmount }
        val totalPnl = totalEval - totalCost
        val rate = if (totalCost <= 0) 0.0 else totalPnl.toDouble() / totalCost

        return HoldingSummary(
            totalCost = totalCost,
            totalEval = totalEval,
            totalPnl = totalPnl,
            totalReturnRate = rate,
            holdings = holdings.sortedByDescending { it.evalPnl },
        )
    }
}
