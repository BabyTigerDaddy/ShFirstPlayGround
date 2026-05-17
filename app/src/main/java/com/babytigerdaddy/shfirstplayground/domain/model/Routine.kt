package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 일일 routine 한 단위 (e.g. 아침 routine, 저녁 routine).
 * Phase 1 prototype은 2개 (morning, evening) 고정 + 기본 step 템플릿 제공.
 */
data class Routine(
    /** 안정 ID. e.g. "morning", "evening". */
    val id: String,
    /** 표시 제목. e.g. "아침 routine". */
    val title: String,
    /** 정렬 순서 (1-based). 같은 날 내에서 아침 → 저녁 순. */
    val displayOrder: Int,
    /** 이 routine의 step 템플릿. 사용자는 step을 추가/삭제하지 않고 체크만. */
    val steps: List<RoutineStep>,
)
