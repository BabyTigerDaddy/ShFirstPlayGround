package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 월별 목표 손익 — 대시보드 달성률 링의 기준.
 *
 * 한 달에 하나(yearMonth가 ID). 같은 달 다시 저장하면 덮어씀.
 */
data class MonthlyGoal(
    /** "YYYY-MM". */
    val yearMonth: String,
    /** 목표 실현손익(원). */
    val target: Long,
)
