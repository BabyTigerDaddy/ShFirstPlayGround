package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.StockMasterDao
import com.babytigerdaddy.shfirstplayground.data.local.database.StockMasterEntity
import com.babytigerdaddy.shfirstplayground.domain.model.StockMaster
import com.babytigerdaddy.shfirstplayground.domain.repository.StockMasterRepository
import javax.inject.Inject
import javax.inject.Singleton

/** StockMasterRepository Room-backed 구현. */
@Singleton
class RoomStockMasterRepository @Inject constructor(
    private val dao: StockMasterDao,
) : StockMasterRepository {

    override suspend fun search(query: String): List<StockMaster> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return dao.search(q).map(StockMasterEntity::toDomain)
    }

    override suspend fun count(): Int = dao.count()

    override suspend fun saveAll(stocks: List<StockMaster>) {
        dao.upsertAll(stocks.map(StockMasterEntity::fromDomain))
    }
}
