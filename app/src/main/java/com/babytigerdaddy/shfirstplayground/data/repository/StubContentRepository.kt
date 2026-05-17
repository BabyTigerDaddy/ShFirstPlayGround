package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Content
import com.babytigerdaddy.shfirstplayground.domain.model.Genre
import com.babytigerdaddy.shfirstplayground.domain.model.Language
import com.babytigerdaddy.shfirstplayground.domain.model.Theme
import com.babytigerdaddy.shfirstplayground.domain.repository.ContentRepository
import javax.inject.Inject

/**
 * UI scaffold 검증용 임시 stub. 실 구현(정적 JSON / Room)은 소보고가 갈아끼움.
 */
class StubContentRepository @Inject constructor() : ContentRepository {

    private val seed = listOf(
        Content(
            id = "kongsoonyi",
            title = "콩순이",
            aliases = listOf("kongsoonyi", "콩순이와 친구들"),
            genre = Genre.STORY,
            themes = listOf(Theme.FAMILY, Theme.EVERYDAY, Theme.FRIENDSHIP),
            language = Language.KOREAN,
            characters = listOf("콩순이"),
            summary = "5살 또래 일상 + 친구·가족 정서 묘사.",
        ),
        Content(
            id = "superwings",
            title = "출동! 슈퍼윙스",
            aliases = listOf("super wings", "슈퍼윙스"),
            genre = Genre.ANIMATION,
            themes = listOf(Theme.ADVENTURE, Theme.NATURE, Theme.LANGUAGE),
            language = Language.KOREAN,
            characters = listOf("호기", "도니"),
            summary = "비행기 캐릭터가 세계 여러 나라를 돌며 친구를 돕는 이야기.",
        ),
        Content(
            id = "bluey",
            title = "Bluey",
            aliases = listOf("블루이"),
            genre = Genre.ANIMATION,
            themes = listOf(Theme.FAMILY, Theme.IMAGINATION, Theme.EVERYDAY),
            language = Language.ENGLISH,
            characters = listOf("Bluey", "Bingo", "Bandit", "Chilli"),
            summary = "호주 블루 힐러 강아지 가족의 따뜻한 일상.",
        ),
        Content(
            id = "numberblocks",
            title = "Numberblocks",
            aliases = listOf("넘버블록스"),
            genre = Genre.EDUCATIONAL,
            themes = listOf(Theme.NUMBERS, Theme.LANGUAGE),
            language = Language.ENGLISH,
            characters = listOf("One", "Two", "Three"),
            summary = "숫자가 캐릭터로 등장하는 수학 학습 애니메이션.",
        ),
    )

    override suspend fun findContent(query: String): List<Content> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return seed
        return seed.filter { content ->
            content.title.lowercase().contains(q) ||
                content.aliases.any { it.lowercase().contains(q) }
        }
    }

    override suspend fun getContent(id: String): Content? = seed.find { it.id == id }
}
