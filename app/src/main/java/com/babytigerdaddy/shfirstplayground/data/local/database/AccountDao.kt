package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM account ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT COUNT(*) FROM account")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AccountEntity)

    @Query("DELETE FROM account WHERE id = :id")
    suspend fun delete(id: String)

    /** 백업용 — 전체 일회성 조회. */
    @Query("SELECT * FROM account")
    suspend fun getAll(): List<AccountEntity>

    /** 복원용 — 전체 삭제(클라우드 데이터로 덮어쓰기 전). */
    @Query("DELETE FROM account")
    suspend fun clearAll()

    /** 계좌 현금 잔액 갱신. */
    @Query("UPDATE account SET cash = :cash WHERE id = :id")
    suspend fun updateCash(id: String, cash: Long)
}
