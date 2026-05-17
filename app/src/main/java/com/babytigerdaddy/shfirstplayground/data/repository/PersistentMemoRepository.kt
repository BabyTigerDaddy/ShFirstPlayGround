package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.data.local.AppStateStore
import com.babytigerdaddy.shfirstplayground.domain.model.Memo
import com.babytigerdaddy.shfirstplayground.domain.repository.MemoRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MemoRepository DataStore-backed 구현. v3 phase 1 영속.
 * InMemoryMemoRepository 대체.
 */
@Singleton
class PersistentMemoRepository @Inject constructor(
    private val store: AppStateStore,
) : MemoRepository {

    override suspend fun getMemoForDate(date: LocalDate): Memo? = store.getMemo(date)

    override suspend fun saveMemo(memo: Memo) {
        store.saveMemo(memo)
    }

    override suspend fun countMemosBetween(start: LocalDate, endInclusive: LocalDate): Int =
        store.countMemosBetween(start, endInclusive)
}
