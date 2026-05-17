package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyLogDao {

    @Query("SELECT * FROM happy_log ORDER BY occurredAt DESC")
    fun observeAll(): Flow<List<HappyLogEntity>>

    @Query("SELECT * FROM happy_log WHERE date(occurredAt) = :date ORDER BY occurredAt DESC")
    suspend fun listByDate(date: String): List<HappyLogEntity>

    @Query("SELECT * FROM happy_log WHERE id = :id")
    suspend fun getById(id: String): HappyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HappyLogEntity)
}
