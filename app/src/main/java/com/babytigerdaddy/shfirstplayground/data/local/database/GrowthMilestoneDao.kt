package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthMilestoneDao {

    @Query("SELECT * FROM growth_milestone ORDER BY recordedOn DESC")
    fun observeAll(): Flow<List<GrowthMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: GrowthMilestoneEntity)
}
