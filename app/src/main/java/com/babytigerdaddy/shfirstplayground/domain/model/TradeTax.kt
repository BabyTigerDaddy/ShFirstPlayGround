package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 매매 세금·수수료 — 세후 실질 손익 계산용(키움 등 증권사 '제비용 포함 손익'과 같은 개념).
 *
 * 2026년 기준: 매도 시 증권거래세 0.20%(코스피 0.05%+농특세 0.15%, 코스닥 0.20% — 둘 다 0.20%),
 * 매매 수수료 0.015%(매수·매도 각각, 키움 기준). 손절이어도 거래세는 부과된다.
 */
object TradeTax {
    /** 매도 시 증권거래세(+농특세) 0.20%. */
    const val SELL_TAX_RATE = 0.0020
    /** 매매 수수료 0.015% — 매수·매도 각각. */
    const val FEE_RATE = 0.00015

    /**
     * 총 제비용(원) = 매수 수수료 + 매도 수수료 + 매도 거래세.
     * @param buyAmount 매수금액(매수가×수량), @param sellAmount 매도(또는 현재 평가)금액.
     */
    fun totalCost(buyAmount: Long, sellAmount: Long): Long {
        val buyFee = buyAmount * FEE_RATE
        val sellFee = sellAmount * FEE_RATE
        val sellTax = sellAmount * SELL_TAX_RATE
        return (buyFee + sellFee + sellTax).toLong()
    }
}
