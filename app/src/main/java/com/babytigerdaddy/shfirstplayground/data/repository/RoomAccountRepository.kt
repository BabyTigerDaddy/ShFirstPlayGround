package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.database.AccountDao
import com.babytigerdaddy.shfirstplayground.data.local.database.AccountEntity
import com.babytigerdaddy.shfirstplayground.domain.model.Account
import com.babytigerdaddy.shfirstplayground.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** AccountRepository Room-backed 구현. */
@Singleton
class RoomAccountRepository @Inject constructor(
    private val dao: AccountDao,
) : AccountRepository {

    override fun observeAll(): Flow<List<Account>> =
        dao.observeAll().map { list -> list.map(AccountEntity::toDomain) }

    override suspend fun count(): Int = dao.count()

    override suspend fun save(account: Account) {
        dao.upsert(AccountEntity.fromDomain(account))
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
