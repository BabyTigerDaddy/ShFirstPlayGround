package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * Routine 안의 한 step. e.g. 아침 routine의 "세수하기", "아침 식사".
 *
 * 완료 상태 자체는 도메인 모델 X — 일별 RoutineLog 결로 별도 트래킹 (Phase 1 retention 측정).
 */
data class RoutineStep(
    /** 안정 ID. e.g. "morning_wash". */
    val id: String,
    /** 상위 Routine.id. */
    val routineId: String,
    /** 표시 제목. e.g. "세수하기". */
    val title: String,
    /** Routine 안의 정렬 순서 (1-based). */
    val order: Int,
)
