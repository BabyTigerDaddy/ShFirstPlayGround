package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Episode
import com.babytigerdaddy.shfirstplayground.domain.model.EpisodeQuestion

/**
 * v2 에피소드 + 큐레이션 질문 조회 인터페이스.
 *
 * v1 ContentRepository와 별도 — v1은 fuzzy 매칭 라이브러리, v2는 사전 큐레이션 단일 에피.
 * v2 검증 통과 후 v1 deprecate 예정.
 */
interface EpisodeRepository {

    /** 전체 에피 목록. v2 prototype 단계는 Bluey S1E1 1개만 반환. */
    suspend fun listEpisodes(): List<Episode>

    /** id로 단건 에피 조회. */
    suspend fun getEpisode(id: String): Episode?

    /** 해당 에피의 큐레이션 질문들. displayOrder 오름차순. */
    suspend fun getQuestions(episodeId: String): List<EpisodeQuestion>
}
