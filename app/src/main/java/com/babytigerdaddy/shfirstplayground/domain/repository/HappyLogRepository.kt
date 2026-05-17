package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.HappyLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 행복한 로그 저장·조회. v4 phase 1은 in-memory, 다음 commit에서 Room 영속.
 */
interface HappyLogRepository {

    /** 전체 로그 (occurredAt 내림차순). */
    fun observeAll(): Flow<List<HappyLog>>

    /** 주어진 날짜의 로그. */
    suspend fun listByDate(date: LocalDate): List<HappyLog>

    /** 신규 로그 저장. */
    suspend fun save(log: HappyLog)

    /** id로 단건 조회. */
    suspend fun getById(id: String): HappyLog?
}
