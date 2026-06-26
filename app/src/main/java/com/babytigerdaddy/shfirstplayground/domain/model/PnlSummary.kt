package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDate

/**
 * 추이 화면이 쓰는 집계 결과 묶음 — 일지 엔트리들을 굴려서 만든 "단타 추이" 한 장.
 *
 * v5 추이(trend) 도메인의 출력. UI(소문서)는 이 값만 받아 그래프·요약을 그림.
 * 계산 책임은 [com.babytigerdaddy.shfirstplayground.domain.usecase.PnlCalculator].
 */
data class PnlSummary(
    /** 누적 실현손익 합계(원). */
    val totalRealized: Long,
    /** 기록한 날 수(무매매 0원 날 포함). */
    val recordedDays: Int,
    /** 이익 난 날 수. */
    val winDays: Int,
    /** 손실 난 날 수. */
    val lossDays: Int,
    /** 승률 = 이익 난 날 / (이익+손실 날). 매매한 날이 없으면 0. 0~1. */
    val winRate: Double,
    /** 가장 많이 번 날(없으면 null). */
    val bestDay: DailyPnlPoint?,
    /** 가장 많이 잃은 날(없으면 null). */
    val worstDay: DailyPnlPoint?,
    /** 현재 연속 기록 — 오늘 기준 직전까지 연속 이익(+) 또는 연속 손실(-) 일수. */
    val currentStreak: Int,
    /** 일별 시계열(오름차순) — 누적선 그래프용. */
    val daily: List<DailyPnlPoint>,
    /** 월별 합계(오름차순) — 막대 그래프용. */
    val monthly: List<MonthlyPnl>,
) {
    companion object {
        /** 데이터 없을 때 빈 요약. */
        val EMPTY = PnlSummary(
            totalRealized = 0,
            recordedDays = 0,
            winDays = 0,
            lossDays = 0,
            winRate = 0.0,
            bestDay = null,
            worstDay = null,
            currentStreak = 0,
            daily = emptyList(),
            monthly = emptyList(),
        )
    }
}

/** 하루 점 하나 — 그날 실현손익 + 그날까지의 누적 + 그날 한 줄 메모. */
data class DailyPnlPoint(
    val date: LocalDate,
    val realizedPnl: Long,
    /** 첫날부터 이 날까지 누적 실현손익. */
    val cumulative: Long,
    /** 그날 한 줄 일기 — best/worst 카드나 점 tooltip에 같이 띄움. */
    val note: String = "",
)

/** 한 달 합계 — yearMonth는 "YYYY-MM". */
data class MonthlyPnl(
    val yearMonth: String,
    val total: Long,
    val recordedDays: Int,
    val winDays: Int,
)
