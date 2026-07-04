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
 *
 * [sectorOf]는 종목 → 업종(큰 섹터) 함수. 이걸로 종목들을 업종별로도 묶어 [AssetAllocation.sectorSlices]에
 * 담는다(기본값은 전부 '기타' — 종목 마스터 업종이 아직 없을 때). 화면이 종목별/업종별을 토글해 보인다.
 */
object AllocationCalculator {

    fun compute(
        holdings: List<Holding>,
        cash: Long = 0,
        sectorOf: (Holding) -> String = { "기타" },
    ): AssetAllocation {
        if (holdings.isEmpty() && cash <= 0) return AssetAllocation.EMPTY

        val byTicker = holdings.groupBy { it.ticker }
            .map { (ticker, list) -> ticker to list.sumOf { it.evalAmount } }
        val totalEval = byTicker.sumOf { it.second } + cash

        fun ratioOf(amount: Long) = if (totalEval > 0) amount.toDouble() / totalEval else 0.0

        val stockSlices = byTicker.map { (ticker, amount) ->
            AllocationSlice(ticker = ticker, evalAmount = amount, ratio = ratioOf(amount))
        }
        val cashSlice = if (cash > 0) {
            listOf(AllocationSlice("현금", cash, ratioOf(cash)))
        } else {
            emptyList()
        }
        val slices = (stockSlices + cashSlice).sortedByDescending { it.ratio }

        // 업종별 — 종목을 큰 섹터로 묶어 합산(현금은 '현금' 조각 그대로).
        val sectorStockSlices = holdings.groupBy { sectorOf(it) }
            .map { (sector, list) ->
                val amount = list.sumOf { it.evalAmount }
                AllocationSlice(ticker = sector, evalAmount = amount, ratio = ratioOf(amount))
            }
        val sectorSlices = (sectorStockSlices + cashSlice).sortedByDescending { it.ratio }

        val top = slices.firstOrNull()
        return AssetAllocation(
            totalEval = totalEval,
            slices = slices,
            sectorSlices = sectorSlices,
            concentrationTicker = top?.ticker,
            concentrationRatio = top?.ratio ?: 0.0,
        )
    }
}
