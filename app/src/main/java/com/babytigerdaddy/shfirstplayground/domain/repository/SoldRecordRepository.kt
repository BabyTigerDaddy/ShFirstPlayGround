package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import kotlinx.coroutines.flow.Flow

/** 매도 내역 저장·조회. */
interface SoldRecordRepository {

    /** 전체 매도 내역(매도일 오름차순). */
    fun observeAll(): Flow<List<SoldRecord>>

    /** 매도 내역 저장. */
    suspend fun save(record: SoldRecord)

    /** 삭제(잘못 기록 정정). */
    suspend fun delete(id: String)
}
