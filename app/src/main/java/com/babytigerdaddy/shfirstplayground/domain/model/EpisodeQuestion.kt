package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 에피소드 특화 메타인지 질문. v2 핵심 가치 — plot-aware 사전 큐레이션 질문.
 *
 * v1 template fill과 다른 점: 실제 에피소드 plot·캐릭터·상황을 reference하는 full text.
 * placeholder 없음. 부모가 그대로 아이에게 던질 수 있는 완성된 문장.
 */
data class EpisodeQuestion(
    /** 안정 ID. e.g. "bluey_s01e01_q1". */
    val id: String,
    /** 소속 Episode.id. */
    val episodeId: String,
    /** 카테고리 (5종 정서·인지·회상·적용·비교). */
    val category: QuestionCategory,
    /** 부모가 그대로 던질 수 있는 완성된 질문 문장. */
    val text: String,
    /**
     * 부모용 컨텍스트: 이 질문이 어떤 plot 순간·발달 변수를 짚는지 1줄.
     * 시청 후 부모가 아이 답변 듣고 follow-up 할 때 참고.
     */
    val context: String,
    /** 카드 표시 순서 (1-based). */
    val displayOrder: Int,
)
