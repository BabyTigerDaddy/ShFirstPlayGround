package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDate

/**
 * 부모가 그날 아이에 대해 짧게 남긴 메모.
 * Phase 1 success criteria (c) "메모 입력 평균 3회/주" 측정 단위.
 */
data class Memo(
    /** 안정 ID. */
    val id: String,
    /** 메모 대상 날짜. */
    val date: LocalDate,
    /** 오늘 잘한 점 — 선택 입력. */
    val highlight: String?,
    /** 오늘 도전한 점 — 선택 입력. */
    val challenge: String?,
)
