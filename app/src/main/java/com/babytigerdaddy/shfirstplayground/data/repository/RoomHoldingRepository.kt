package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.HoldingDao
import com.babytigerdaddy.shfirstplayground.data.local.database.HoldingEntity
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** HoldingRepository Room-backed 구현. */
@Singleton
class RoomHoldingRepository @Inject constructor(
    private val dao: HoldingDao,
) : HoldingRepository {

    override fun observeAll(): Flow<List<Holding>> =
        dao.observeAll().map { list -> list.map(HoldingEntity::toDomain) }

    override suspend fun getById(id: String): Holding? =
        dao.getById(id)?.toDomain()

    override suspend fun save(holding: Holding) {
        dao.upsert(HoldingEntity.fromDomain(holding))
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
