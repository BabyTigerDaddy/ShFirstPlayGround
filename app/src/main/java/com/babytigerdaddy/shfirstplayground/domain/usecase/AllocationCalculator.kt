package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.AllocationSlice
import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.Holding

/**
 * 보유 종목 → 자산 배분([AssetAllocation]) 순수 변환기.
 * 같은 종목명은 합산, 비중 = 종목 평가금액 ÷ 총 평가금액.
 *
 * [cash]는 현금 보유액 — 총 자산(분모)에 포함하고 '현금' 조각으로 넣어 자산 배분에 비중을 보인다.
 * (수익률 계산과는 무관 — 배분에만 반영.)
 */
object AllocationCalculator {

    fun compute(holdings: List<Holding>, cash: Long = 0): AssetAllocation {
        if (holdings.isEmpty() && cash <= 0) return AssetAllocation.EMPTY

        val byTicker = holdings.groupBy { it.ticker }
            .map { (ticker, list) -> ticker to list.sumOf { it.evalAmount } }
        val totalEval = byTicker.sumOf { it.second } + cash

        val stockSlices = byTicker.map { (ticker, amount) ->
            AllocationSlice(
                ticker = ticker,
                evalAmount = amount,
                ratio = if (totalEval > 0) amount.toDouble() / totalEval else 0.0,
            )
        }
        val cashSlice = if (cash > 0) {
            listOf(AllocationSlice("현금", cash, if (totalEval > 0) cash.toDouble() / totalEval else 0.0))
        } else {
            emptyList()
        }
        val slices = (stockSlices + cashSlice).sortedByDescending { it.ratio }

        val top = slices.firstOrNull()
        return AssetAllocation(
            totalEval = totalEval,
            slices = slices,
            concentrationTicker = top?.ticker,
            concentrationRatio = top?.ratio ?: 0.0,
        )
    }
}
