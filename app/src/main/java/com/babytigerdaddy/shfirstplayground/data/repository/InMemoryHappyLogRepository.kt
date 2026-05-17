package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.domain.model.HappyLog
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HappyLogRepository in-memory 구현 — v4 phase 1 prototype.
 * 다음 commit에서 Room 영속화로 교체 예정.
 */
@Singleton
class InMemoryHappyLogRepository @Inject constructor() : HappyLogRepository {

    private val mutex = Mutex()
    private val state = MutableStateFlow<List<HappyLog>>(emptyList())

    override fun observeAll(): Flow<List<HappyLog>> = state.asStateFlow()

    override suspend fun listByDate(date: LocalDate): List<HappyLog> = mutex.withLock {
        state.value.filter { it.occurredAt.toLocalDate() == date }
    }

    override suspend fun save(log: HappyLog): Unit = mutex.withLock {
        state.update { current ->
            val filtered = current.filter { it.id != log.id }
            (filtered + log).sortedByDescending { it.occurredAt }
        }
    }

    override suspend fun getById(id: String): HappyLog? = mutex.withLock {
        state.value.find { it.id == id }
    }
}
