package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyGoalDao {

    @Query("SELECT * FROM monthly_goal ORDER BY yearMonth ASC")
    fun observeAll(): Flow<List<MonthlyGoalEntity>>

    @Query("SELECT * FROM monthly_goal WHERE yearMonth = :yearMonth")
    suspend fun getByMonth(yearMonth: String): MonthlyGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MonthlyGoalEntity)
}
