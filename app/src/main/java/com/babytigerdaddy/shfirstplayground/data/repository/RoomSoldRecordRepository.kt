package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.SoldRecordDao
import com.babytigerdaddy.shfirstplayground.data.local.database.SoldRecordEntity
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** SoldRecordRepository Room-backed 구현. */
@Singleton
class RoomSoldRecordRepository @Inject constructor(
    private val dao: SoldRecordDao,
) : SoldRecordRepository {

    override fun observeAll(): Flow<List<SoldRecord>> =
        dao.observeAll().map { list -> list.map(SoldRecordEntity::toDomain) }

    override suspend fun save(record: SoldRecord) {
        dao.upsert(SoldRecordEntity.fromDomain(record))
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
