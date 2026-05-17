package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 콘텐츠 metadata. 5살 부모용 콘텐츠 라이브러리 한 항목.
 *
 * 실제 데이터 스키마·필드 보강은 소보고(sh-briefing) 담당. 본 클래스는 UI scaffold용 placeholder.
 */
data class Content(
    val id: String,
    val title: String,
    val aliases: List<String> = emptyList(),
    val genre: Genre,
    val themes: List<Theme>,
    val ageRange: IntRange = 3..6,
    val language: Language,
    val characters: List<String> = emptyList(),
    val summary: String = "",
)

enum class Genre {
    ANIMATION, EDUCATIONAL, MUSIC, STORY, MIXED,
}

enum class Theme {
    FAMILY, FRIENDSHIP, ADVENTURE, EMOTION, NATURE, NUMBERS, LANGUAGE, EVERYDAY, IMAGINATION,
}

enum class Language {
    KOREAN, ENGLISH, BILINGUAL,
}
