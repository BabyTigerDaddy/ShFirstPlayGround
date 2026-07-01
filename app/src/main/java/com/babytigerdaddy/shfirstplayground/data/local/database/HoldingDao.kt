package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Query("SELECT * FROM holding ORDER BY entryDate ASC")
    fun observeAll(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holding WHERE id = :id")
    suspend fun getById(id: String): HoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HoldingEntity)

    @Query("DELETE FROM holding WHERE id = :id")
    suspend fun delete(id: String)

    /** 백업용 — 전체 일회성 조회. */
    @Query("SELECT * FROM holding")
    suspend fun getAll(): List<HoldingEntity>

    /** 복원용 — 전체 삭제(클라우드 데이터로 덮어쓰기 전). */
    @Query("DELETE FROM holding")
    suspend fun clearAll()
}
