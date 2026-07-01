package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 자산 배분 — 지금 들고 있는 종목이 전체 평가금액에서 각각 몇 %인지.
 *
 * 원 그래프 + '집중 종목'(제일 몰린 종목)으로 몰빵을 한눈에 경계하게 한다.
 * 같은 종목을 여러 번 담았으면 종목명 기준으로 합산한다.
 */
data class AssetAllocation(
    /** 총 평가금액(원). */
    val totalEval: Long,
    /** 종목별 조각(비중 내림차순). */
    val slices: List<AllocationSlice>,
    /** 제일 큰 비중 종목명(없으면 null). */
    val concentrationTicker: String?,
    /** 그 종목의 비중(0~1). */
    val concentrationRatio: Double,
) {
    companion object {
        val EMPTY = AssetAllocation(0, emptyList(), null, 0.0)
    }
}

/** 자산 배분 원 그래프의 한 조각. */
data class AllocationSlice(
    val ticker: String,
    /** 그 종목 평가금액 합(원). */
    val evalAmount: Long,
    /** 전체 대비 비중(0~1). */
    val ratio: Double,
)
