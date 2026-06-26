package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.MonthlyGoalDao
import com.babytigerdaddy.shfirstplayground.data.local.database.MonthlyGoalEntity
import com.babytigerdaddy.shfirstplayground.domain.model.MonthlyGoal
import com.babytigerdaddy.shfirstplayground.domain.repository.MonthlyGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** MonthlyGoalRepository Room-backed 구현. */
@Singleton
class RoomMonthlyGoalRepository @Inject constructor(
    private val dao: MonthlyGoalDao,
) : MonthlyGoalRepository {

    override fun observeAll(): Flow<List<MonthlyGoal>> =
        dao.observeAll().map { list -> list.map(MonthlyGoalEntity::toDomain) }

    override suspend fun getByMonth(yearMonth: String): MonthlyGoal? =
        dao.getByMonth(yearMonth)?.toDomain()

    override suspend fun save(goal: MonthlyGoal) {
        dao.upsert(MonthlyGoalEntity.fromDomain(goal))
    }
}
