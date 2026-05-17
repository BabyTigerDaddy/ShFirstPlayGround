package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Routine

/**
 * Routine 템플릿 조회. v3 Phase 1은 morning/evening 2개 정적 템플릿.
 */
interface RoutineRepository {

    /** 전체 routine 목록 (displayOrder 오름차순). */
    suspend fun listRoutines(): List<Routine>

    /** id로 단건 조회. */
    suspend fun getRoutine(id: String): Routine?
}
