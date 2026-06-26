package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeJournalDao {

    @Query("SELECT * FROM trade_journal ORDER BY date ASC")
    fun observeAll(): Flow<List<TradeJournalEntity>>

    @Query("SELECT * FROM trade_journal WHERE date = :date")
    suspend fun getByDate(date: String): TradeJournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TradeJournalEntity)

    @Query("DELETE FROM trade_journal WHERE date = :date")
    suspend fun delete(date: String)
}
