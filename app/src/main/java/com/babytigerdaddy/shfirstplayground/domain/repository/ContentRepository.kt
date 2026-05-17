package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Content

/**
 * 콘텐츠 라이브러리 조회 인터페이스.
 *
 * 구현 (정적 JSON / Room DB)은 소보고(sh-briefing) 담당.
 */
interface ContentRepository {

    /** fuzzy match로 콘텐츠 검색. 빈 query면 추천 목록 반환. */
    suspend fun findContent(query: String): List<Content>

    /** id로 단건 조회. */
    suspend fun getContent(id: String): Content?
}
