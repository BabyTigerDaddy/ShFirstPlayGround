package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 보유 종목 한 단위 — 아직 안 팔고 들고 있는 종목의 평가손익 추적.
 *
 * 매매일지(TradeJournalEntry, 이미 판 결과)와는 분리된 '현재진행형' 도메인.
 * 매도하면 [com.babytigerdaddy.shfirstplayground.domain.usecase.SellHoldingUseCase]가
 * 실현손익을 매매일지로 넘기고 이 보유분은 비운다.
 */
data class Holding(
    /** 안정 ID — 같은 종목 여러 번 담을 수 있어 날짜/종목이 아닌 UUID. */
    val id: String,
    /** 소속 계좌 id. */
    val accountId: String = Account.DEFAULT_ID,
    /** 종목명. */
    val ticker: String,
    /** 매수가(원, 1주). */
    val buyPrice: Long,
    /** 현재가(원, 1주) — 수동 갱신. */
    val currentPrice: Long,
    /** 수량(주). 기본 1. */
    val quantity: Int = 1,
    /** 편입일. */
    val entryDate: LocalDate,
    val createdAt: LocalDateTime,
) {
    /** 수익률 — (현재가-매수가)/매수가. 매수가 0이면 0. 수량 무관. */
    val returnRate: Double
        get() = if (buyPrice <= 0) 0.0 else (currentPrice - buyPrice).toDouble() / buyPrice

    /** 평가손익(원) = (현재가-매수가) × 수량. */
    val evalPnl: Long get() = (currentPrice - buyPrice) * quantity

    /** 현재 평가금액(원) = 현재가 × 수량. */
    val evalAmount: Long get() = currentPrice * quantity

    /** 매수금액(원) = 매수가 × 수량. */
    val costAmount: Long get() = buyPrice * quantity

    /** 지금 팔면 나가는 예상 세금·수수료(원). */
    val estimatedFee: Long get() = TradeTax.totalCost(costAmount, evalAmount)

    /** 세후 평가손익(원) — 지금 팔면 세금·수수료 떼고 실제로 남는 손익. */
    val netEvalPnl: Long get() = evalPnl - estimatedFee

    /** 세후 수익률 — netEvalPnl / 매수금액. 매수가 0이면 0. */
    val netReturnRate: Double
        get() = if (costAmount <= 0) 0.0 else netEvalPnl.toDouble() / costAmount

    /** 보유일 — 편입일부터 [asOf]까지 거래일(주말 토·일 제외). 편입 당일은 0. */
    fun holdingDays(asOf: LocalDate): Long =
        BusinessDays.between(entryDate, asOf)
}

/**
 * 보유 종목 전체 집계 — '보유 종목' 탭 헤더용.
 */
data class HoldingSummary(
    /** 총 매수금액(원). */
    val totalCost: Long,
    /** 총 평가금액(원). */
    val totalEval: Long,
    /** 총 평가손익(원). */
    val totalPnl: Long,
    /** 전체 수익률 — totalPnl/totalCost. 0이면 0. */
    val totalReturnRate: Double,
    /** 보유 종목들(평가손익 내림차순). */
    val holdings: List<Holding>,
) {
    companion object {
        val EMPTY = HoldingSummary(0, 0, 0, 0.0, emptyList())
    }
}
