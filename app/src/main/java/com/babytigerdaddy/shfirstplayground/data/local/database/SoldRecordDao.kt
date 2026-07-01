package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoldRecordDao {

    @Query("SELECT * FROM sold_record ORDER BY soldDate ASC")
    fun observeAll(): Flow<List<SoldRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoldRecordEntity)

    @Query("DELETE FROM sold_record WHERE id = :id")
    suspend fun delete(id: String)

    /** 백업용 — 전체 일회성 조회. */
    @Query("SELECT * FROM sold_record")
    suspend fun getAll(): List<SoldRecordEntity>

    /** 복원용 — 전체 삭제(클라우드 데이터로 덮어쓰기 전). */
    @Query("DELETE FROM sold_record")
    suspend fun clearAll()
}
