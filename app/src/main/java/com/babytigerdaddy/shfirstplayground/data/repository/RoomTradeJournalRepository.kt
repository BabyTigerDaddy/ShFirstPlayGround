package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.TradeJournalDao
import com.babytigerdaddy.shfirstplayground.data.local.database.TradeJournalEntity
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TradeJournalRepository Room-backed 구현 — v5 영속.
 */
@Singleton
class RoomTradeJournalRepository @Inject constructor(
    private val dao: TradeJournalDao,
) : TradeJournalRepository {

    override fun observeAll(): Flow<List<TradeJournalEntry>> =
        dao.observeAll().map { list -> list.map(TradeJournalEntity::toDomain) }

    override suspend fun getByDate(date: LocalDate): TradeJournalEntry? =
        dao.getByDate(date.toString())?.toDomain()

    override suspend fun save(entry: TradeJournalEntry) {
        dao.upsert(TradeJournalEntity.fromDomain(entry))
    }

    override suspend fun delete(date: LocalDate) {
        dao.delete(date.toString())
    }
}
