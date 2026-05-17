package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 메타인지 질문 카테고리 5종. UI에서 카테고리별 색깔 차별화에 사용.
 *
 * - 정서: 캐릭터·상황의 감정 인식
 * - 인지: 사건의 원인·결과 이해
 * - 회상: 콘텐츠 내용 기억·요약
 * - 적용: 일상에서 비슷한 상황 떠올리기
 * - 비교: 다른 콘텐츠·경험과 비교
 */
enum class QuestionCategory {
    EMOTION,
    COGNITION,
    RECALL,
    APPLICATION,
    COMPARISON,
}
