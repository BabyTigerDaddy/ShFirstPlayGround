package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Account
import kotlinx.coroutines.flow.Flow

/** 계좌 저장·조회. */
interface AccountRepository {

    /** 전체 계좌(sortOrder 오름차순). */
    fun observeAll(): Flow<List<Account>>

    /** 현재 계좌 수(기본 계좌 보장용). */
    suspend fun count(): Int

    /** 저장(신규/이름 수정). */
    suspend fun save(account: Account)

    /** 삭제. */
    suspend fun delete(id: String)
}
