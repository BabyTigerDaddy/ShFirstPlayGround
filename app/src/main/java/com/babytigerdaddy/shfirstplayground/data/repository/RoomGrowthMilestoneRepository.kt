package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.GrowthMilestoneDao
import com.babytigerdaddy.shfirstplayground.data.local.database.GrowthMilestoneEntity
import com.babytigerdaddy.shfirstplayground.domain.model.GrowthMilestone
import com.babytigerdaddy.shfirstplayground.domain.repository.GrowthMilestoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomGrowthMilestoneRepository @Inject constructor(
    private val dao: GrowthMilestoneDao,
) : GrowthMilestoneRepository {

    override fun observeAll(): Flow<List<GrowthMilestone>> =
        dao.observeAll().map { list -> list.map(GrowthMilestoneEntity::toDomain) }

    override suspend fun save(milestone: GrowthMilestone) {
        dao.upsert(GrowthMilestoneEntity.fromDomain(milestone))
    }
}
