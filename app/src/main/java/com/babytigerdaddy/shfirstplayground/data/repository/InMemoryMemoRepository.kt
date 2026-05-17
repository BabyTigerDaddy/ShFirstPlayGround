package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Memo
import com.babytigerdaddy.shfirstplayground.domain.repository.MemoRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 메모 in-memory 저장소. v3 Phase 1 prototype 단계 단순화.
 * Phase 2에서 Room 영속화로 교체 예정 — 인터페이스는 그대로 유지되니 ViewModel 변경 X.
 */
@Singleton
class InMemoryMemoRepository @Inject constructor() : MemoRepository {

    private val mutex = Mutex()
    private val byDate = mutableMapOf<LocalDate, Memo>()

    override suspend fun getMemoForDate(date: LocalDate): Memo? = mutex.withLock {
        byDate[date]
    }

    override suspend fun saveMemo(memo: Memo): Unit = mutex.withLock {
        byDate[memo.date] = memo
    }

    override suspend fun countMemosBetween(start: LocalDate, endInclusive: LocalDate): Int =
        mutex.withLock {
            byDate.keys.count { !it.isBefore(start) && !it.isAfter(endInclusive) }
        }
}
