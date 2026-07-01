package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockMasterDao {

    /** 이름 또는 코드로 검색 — 이름 부분일치 우선, 코드 앞자리 일치도. 최대 20건. */
    @Query(
        "SELECT * FROM stock_master " +
            "WHERE name LIKE '%' || :q || '%' OR code LIKE :q || '%' " +
            "ORDER BY CASE WHEN name LIKE :q || '%' THEN 0 ELSE 1 END, name ASC " +
            "LIMIT 20",
    )
    suspend fun search(q: String): List<StockMasterEntity>

    @Query("SELECT * FROM stock_master WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): StockMasterEntity?

    @Query("SELECT COUNT(*) FROM stock_master")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<StockMasterEntity>)
}
