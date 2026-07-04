package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.StockMaster

/** Room Entity for StockMaster. 6자리 code가 PK. */
@Entity(tableName = "stock_master")
data class StockMasterEntity(
    @PrimaryKey val code: String,
    val name: String,
    val market: String,
    val sector: String = "기타",
) {
    fun toDomain(): StockMaster = StockMaster(code = code, name = name, market = market, sector = sector)

    companion object {
        fun fromDomain(s: StockMaster): StockMasterEntity =
            StockMasterEntity(code = s.code, name = s.name, market = s.market, sector = s.sector)
    }
}
