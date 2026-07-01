package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.AllocationSlice
import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.Holding

/**
 * 보유 종목 → 자산 배분([AssetAllocation]) 순수 변환기.
 * 같은 종목명은 합산, 비중 = 종목 평가금액 ÷ 총 평가금액.
 */
object AllocationCalculator {

    fun compute(holdings: List<Holding>): AssetAllocation {
        if (holdings.isEmpty()) return AssetAllocation.EMPTY

        val byTicker = holdings.groupBy { it.ticker }
            .map { (ticker, list) -> ticker to list.sumOf { it.evalAmount } }
        val totalEval = byTicker.sumOf { it.second }

        val slices = byTicker
            .map { (ticker, amount) ->
                AllocationSlice(
                    ticker = ticker,
                    evalAmount = amount,
                    ratio = if (totalEval > 0) amount.toDouble() / totalEval else 0.0,
                )
            }
            .sortedByDescending { it.ratio }

        val top = slices.firstOrNull()
        return AssetAllocation(
            totalEval = totalEval,
            slices = slices,
            concentrationTicker = top?.ticker,
            concentrationRatio = top?.ratio ?: 0.0,
        )
    }
}
