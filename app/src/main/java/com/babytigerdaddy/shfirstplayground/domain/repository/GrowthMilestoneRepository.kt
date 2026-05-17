package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.GrowthMilestone
import kotlinx.coroutines.flow.Flow

/**
 * 성장 milestone 조회·저장. v4 phase 1은 in-memory, 다음 commit Room.
 */
interface GrowthMilestoneRepository {

    /** 전체 milestone (recordedOn 내림차순). */
    fun observeAll(): Flow<List<GrowthMilestone>>

    /** 신규 milestone 저장. */
    suspend fun save(milestone: GrowthMilestone)
}
