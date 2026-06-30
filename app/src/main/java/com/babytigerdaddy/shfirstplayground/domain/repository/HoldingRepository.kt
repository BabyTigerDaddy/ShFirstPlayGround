package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import kotlinx.coroutines.flow.Flow

/**
 * 보유 종목 저장·조회. v7 Room-backed.
 */
interface HoldingRepository {

    /** 전체 보유 종목(편입일 오름차순). */
    fun observeAll(): Flow<List<Holding>>

    /** id로 단건 조회. */
    suspend fun getById(id: String): Holding?

    /** 저장(신규/수정). */
    suspend fun save(holding: Holding)

    /** 삭제(매도·삭제 시). */
    suspend fun delete(id: String)
}
