package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 일일 마이크로카드 1장 — 5살 발달 정보 또는 부모 팁.
 *
 * v3 Phase 1: 사전 큐레이션 30장부터 시작, 일별 rotation. Phase 2에서 사용자 행동 기반 추천.
 */
data class MicroCard(
    /** 안정 ID. e.g. "card_emotion_001". */
    val id: String,
    /** 카드 제목 1줄. */
    val title: String,
    /** 본문 2~4문장. 부모가 30초 안에 읽을 수 있는 길이. */
    val body: String,
    /** 발달 카테고리. */
    val category: MicroCardCategory,
)

/** 5살 발달 카테고리 5종 — 마이크로카드 분류 + UI 색깔 차별화에 사용. */
enum class MicroCardCategory {
    EMOTIONAL,
    COGNITIVE,
    PHYSICAL,
    SOCIAL,
    LANGUAGE,
}
