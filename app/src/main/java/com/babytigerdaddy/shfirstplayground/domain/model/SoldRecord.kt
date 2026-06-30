package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 매도 내역 한 단위 — 보유 종목을 팔았을 때 남는 기록.
 *
 * 보유([Holding])를 매도하면 매도 시점 현재가를 [sellPrice]로 박아 이 기록으로 이관한다.
 * '들고 있다 → 팔았다'의 '팔았다' 쪽 장부.
 */
data class SoldRecord(
    val id: String,
    val ticker: String,
    /** 매수가(원, 1주). */
    val buyPrice: Long,
    /** 매도가(원, 1주) — 매도 시점 현재가. */
    val sellPrice: Long,
    val quantity: Int,
    val entryDate: LocalDate,
    val soldDate: LocalDate,
    val createdAt: LocalDateTime,
) {
    /** 실현손익(원) = (매도가-매수가) × 수량. */
    val realizedPnl: Long get() = (sellPrice - buyPrice) * quantity

    /** 수익률 — (매도가-매수가)/매수가. 매수가 0이면 0. */
    val returnRate: Double
        get() = if (buyPrice <= 0) 0.0 else (sellPrice - buyPrice).toDouble() / buyPrice

    /** 매수금액(원). */
    val costAmount: Long get() = buyPrice * quantity

    /** 보유했던 일수(편입~매도, 최소 0). */
    val heldDays: Long get() = ChronoUnit.DAYS.between(entryDate, soldDate).coerceAtLeast(0)

    val isWin: Boolean get() = realizedPnl > 0
}

/**
 * 매도 내역 집계 — '그동안 이만큼 했구나'를 한눈에.
 */
data class SoldHistorySummary(
    /** 누적 실현손익(원). */
    val totalRealized: Long,
    /** 매도 건수. */
    val saleCount: Int,
    /** 이익 매도 건수. */
    val winCount: Int,
    /** 승률 — 이익 매도 / 전체 매도. 0이면 0. */
    val winRate: Double,
    /** 평균 수익률(0~ 비율) — 건별 수익률 단순 평균. */
    val avgReturnRate: Double,
    /** 누적 실현손익 곡선(매도일 오름차순). */
    val cumulative: List<RealizedPoint>,
    /** 가장 많이 번 매도(없으면 null). */
    val bestSale: SoldRecord?,
    /** 매도 내역(최근 매도일 우선). */
    val records: List<SoldRecord>,
) {
    companion object {
        val EMPTY = SoldHistorySummary(0, 0, 0, 0.0, 0.0, emptyList(), null, emptyList())
    }
}

/** 누적 실현손익 곡선의 점 하나. */
data class RealizedPoint(
    val soldDate: LocalDate,
    val realizedPnl: Long,
    val cumulative: Long,
)
