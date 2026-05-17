package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDate

/**
 * 5~7세 아이 성장 milestone — 키·몸무게·이벤트 등 누적 기록.
 *
 * HappyLog와 분리된 모델: HappyLog는 일상 행복 순간, Milestone은 정량적·이정표적 기록.
 */
data class GrowthMilestone(
    val id: String,
    val recordedOn: LocalDate,
    val kind: MilestoneKind,
    /** 키(cm) 또는 몸무게(kg) — kind에 따라 사용 단위 다름. EVENT면 null. */
    val numericValue: Double?,
    /** 이벤트·메모 설명 — e.g. "처음 자전거 탔어요". */
    val description: String? = null,
)

enum class MilestoneKind {
    HEIGHT,
    WEIGHT,
    EVENT,
}
