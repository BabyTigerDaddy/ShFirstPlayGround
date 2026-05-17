package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 메타인지 질문 한 개. 콘텐츠 시청 후 부모가 아이에게 던질 질문.
 *
 * 실제 generator·template 구현은 소보고(sh-briefing) 담당. 본 클래스는 UI scaffold용.
 */
data class Question(
    val id: String,
    val text: String,
    val category: QuestionCategory,
)

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
    EMOTION,       // 정서
    COGNITION,     // 인지
    RECALL,        // 회상
    APPLICATION,   // 적용
    COMPARISON,    // 비교
}
