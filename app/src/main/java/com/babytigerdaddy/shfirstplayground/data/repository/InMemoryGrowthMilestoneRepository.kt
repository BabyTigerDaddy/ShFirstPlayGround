package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.domain.model.GrowthMilestone
import com.babytigerdaddy.shfirstplayground.domain.repository.GrowthMilestoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GrowthMilestoneRepository in-memory — v4 phase 1 prototype.
 * 다음 commit에서 Room 영속화 교체.
 */
@Singleton
class InMemoryGrowthMilestoneRepository @Inject constructor() : GrowthMilestoneRepository {

    private val mutex = Mutex()
    private val state = MutableStateFlow<List<GrowthMilestone>>(emptyList())

    override fun observeAll(): Flow<List<GrowthMilestone>> = state.asStateFlow()

    override suspend fun save(milestone: GrowthMilestone): Unit = mutex.withLock {
        state.update { current ->
            val filtered = current.filter { it.id != milestone.id }
            (filtered + milestone).sortedByDescending { it.recordedOn }
        }
    }
}
