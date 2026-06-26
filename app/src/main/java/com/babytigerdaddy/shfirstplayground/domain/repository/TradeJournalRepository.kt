package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 매매일지 저장·조회. v5 Room-backed.
 *
 * 하루 한 장(날짜 PK)이라 [save]는 같은 날이면 덮어씀.
 */
interface TradeJournalRepository {

    /** 전체 일지 (date 오름차순) — 추이 집계의 입력. */
    fun observeAll(): Flow<List<TradeJournalEntry>>

    /** 주어진 날짜의 일지 한 장(없으면 null). */
    suspend fun getByDate(date: LocalDate): TradeJournalEntry?

    /** 일지 저장 — 같은 날짜면 덮어씀. */
    suspend fun save(entry: TradeJournalEntry)

    /** 일지 삭제. */
    suspend fun delete(date: LocalDate)
}
