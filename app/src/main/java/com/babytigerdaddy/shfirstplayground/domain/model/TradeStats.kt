package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.DayOfWeek

/**
 * 통계 탭이 쓰는 분석 묶음 — 일지 엔트리들을 굴려 만든 "매매 습관" 한 장.
 *
 * 계산 책임은 [com.babytigerdaddy.shfirstplayground.domain.usecase.StatsCalculator].
 * UI(소문서)는 이 값만 받아 요일 바·종목 순위·인사이트를 그림.
 */
data class TradeStats(
    /** 요일별 승률 7개(월~일 고정 순서). 매매 없는 요일도 0으로 포함. */
    val weekday: List<WeekdayStat>,
    /** 종목별 손익 순위(totalPnl 내림차순). */
    val tickers: List<TickerStat>,
    /** 하루 평균 실현손익(기록일 기준, 무매매 0원 포함). */
    val avgDailyPnl: Long,
    /** 최대 연속 손실 일수(과거 통틀어 가장 길었던 손실 행진). */
    val maxLossStreak: Int,
    /** 최대 낙폭(원) — 누적 곡선 고점 대비 최대 하락폭. 항상 0 이상. */
    val maxDrawdown: Long,
    /** 기분별 손익 — 회고 탭 "기분이 손익에 미친 영향"용(예: 과매매 찍은 날 평균 -8만). */
    val mood: List<MoodStat>,
) {
    companion object {
        val EMPTY = TradeStats(
            weekday = emptyList(),
            tickers = emptyList(),
            avgDailyPnl = 0,
            maxLossStreak = 0,
            maxDrawdown = 0,
            mood = emptyList(),
        )
    }
}

/** 기분 라벨 하나의 집계 — 그 기분으로 기록한 날의 손익 경향. */
data class MoodStat(
    val mood: TradeMood,
    /** 그 기분으로 찍은 날 수. */
    val days: Int,
    /** 그 날들의 평균 실현손익(원). */
    val avgPnl: Long,
    /** 그 날들의 손익 합(원). */
    val totalPnl: Long,
)

/** 요일 하나의 승률. */
data class WeekdayStat(
    val dayOfWeek: DayOfWeek,
    /** 그 요일에 매매한 날 수(이익+손실, 무매매 0원 제외). */
    val tradedDays: Int,
    val winDays: Int,
    /** 승률 0~1. tradedDays 0이면 0. */
    val winRate: Double,
)

/**
 * 종목 하나의 집계.
 *
 * 주의(근사): 일지는 하루 합산 손익이라 한 날에 여러 종목이면 그날 손익이 각 종목에
 * 동일하게 귀속됨 — '그 종목을 매매한 날들의 손익 합'이라는 연관 지표.
 * 정확한 종목별 손익은 추후 종목별 입력(Trade leg)으로 고도화 예정.
 */
data class TickerStat(
    val ticker: String,
    /** 그 종목이 등장한 날 수. */
    val tradedDays: Int,
    /** 그 종목이 등장한 날들의 손익 합(연관 손익). */
    val totalPnl: Long,
)

/**
 * 이번 달 목표 달성률 — 대시보드 원형 링용.
 */
data class GoalProgress(
    /** "YYYY-MM". */
    val yearMonth: String,
    /** 목표 금액(원). 0이면 목표 미설정. */
    val target: Long,
    /** 이번 달 현재 누적 실현손익(원). */
    val current: Long,
    /** 달성률 0~1(과달성도 1.0으로 캡). 목표 미설정이면 0. */
    val ratio: Double,
)
