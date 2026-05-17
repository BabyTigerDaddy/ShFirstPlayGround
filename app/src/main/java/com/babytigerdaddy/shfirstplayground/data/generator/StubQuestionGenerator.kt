package com.babytigerdaddy.shfirstplayground.data.generator

import com.babytigerdaddy.shfirstplayground.domain.generator.QuestionGenerator
import com.babytigerdaddy.shfirstplayground.domain.model.Content
import com.babytigerdaddy.shfirstplayground.domain.model.Question
import com.babytigerdaddy.shfirstplayground.domain.model.QuestionCategory
import javax.inject.Inject

/**
 * UI scaffold 검증용 임시 stub. 실 구현(template matching / 발달 가중치)은 소보고가 갈아끼움.
 */
class StubQuestionGenerator @Inject constructor() : QuestionGenerator {

    override suspend fun generate(content: Content, count: Int): List<Question> {
        val templates = listOf(
            QuestionCategory.EMOTION to "${content.title}에서 가장 기분이 좋아 보였던 장면은 언제였어?",
            QuestionCategory.COGNITION to "왜 그 일이 그렇게 됐을까? 다른 결과도 가능했을까?",
            QuestionCategory.RECALL to "오늘 본 ${content.title}에서 가장 기억나는 장면 한 가지만 말해줄래?",
            QuestionCategory.APPLICATION to "너도 비슷한 일이 있었어? 어떻게 했어?",
            QuestionCategory.COMPARISON to "이 캐릭터랑 너랑 닮은 점이 있어?",
        )
        return templates.take(count).mapIndexed { index, (category, text) ->
            Question(
                id = "${content.id}-q$index",
                text = text,
                category = category,
            )
        }
    }
}
