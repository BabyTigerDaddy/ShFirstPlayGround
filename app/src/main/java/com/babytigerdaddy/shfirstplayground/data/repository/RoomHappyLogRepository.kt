package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.HappyLogDao
import com.babytigerdaddy.shfirstplayground.data.local.database.HappyLogEntity
import com.babytigerdaddy.shfirstplayground.domain.model.HappyLog
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HappyLogRepository Room-backed 구현 — v4 phase 1 영속.
 * InMemoryHappyLogRepository 대체. ViewModel 인터페이스 호출 그대로.
 */
@Singleton
class RoomHappyLogRepository @Inject constructor(
    private val dao: HappyLogDao,
) : HappyLogRepository {

    override fun observeAll(): Flow<List<HappyLog>> =
        dao.observeAll().map { list -> list.map(HappyLogEntity::toDomain) }

    override suspend fun listByDate(date: LocalDate): List<HappyLog> =
        dao.listByDate(date.toString()).map(HappyLogEntity::toDomain)

    override suspend fun save(log: HappyLog) {
        dao.upsert(HappyLogEntity.fromDomain(log))
    }

    override suspend fun getById(id: String): HappyLog? =
        dao.getById(id)?.toDomain()
}
