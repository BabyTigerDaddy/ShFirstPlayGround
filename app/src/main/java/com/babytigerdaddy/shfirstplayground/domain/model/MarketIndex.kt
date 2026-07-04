package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 시장 지수 한 건 — 코스피/코스닥. 상단 티커에 번갈아 보여준다(키움 하단 지수 표시처럼).
 * 예: 코스닥 868.41 ▲1.69 +0.19%
 */
data class MarketIndex(
    /** "코스피" / "코스닥". */
    val name: String,
    /** 현재 지수값(예: 2601.23 / 868.41). */
    val value: Double,
    /** 전일 대비 등락폭(예: +1.69). */
    val change: Double,
    /** 전일 대비 등락률 %(예: +0.19). */
    val changeRate: Double,
) {
    /** 상승(0 이상)이면 true — 빨강, 아니면 파랑. */
    val isUp: Boolean get() = change >= 0.0
}
