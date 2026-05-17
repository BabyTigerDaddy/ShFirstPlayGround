package com.babytigerdaddy.shfirstplayground.domain.generator

import com.babytigerdaddy.shfirstplayground.domain.model.Content
import com.babytigerdaddy.shfirstplayground.domain.model.Question

/**
 * 콘텐츠 metadata 기반 메타인지 질문 generator 인터페이스.
 *
 * 구현 (template matching / LLM 호출)은 소보고(sh-briefing) 담당.
 */
interface QuestionGenerator {

    /** content × 발달 가중치 → 카테고리 균형 잡힌 질문 count개 생성. */
    suspend fun generate(content: Content, count: Int = 3): List<Question>
}
