package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import java.time.LocalDate
import java.time.LocalDateTime

/** Room Entity for SoldRecord. id(UUID)가 PK. */
@Entity(tableName = "sold_record")
data class SoldRecordEntity(
    @PrimaryKey val id: String,
    val ticker: String,
    val buyPrice: Long,
    val sellPrice: Long,
    val quantity: Int,
    val entryDate: LocalDate,
    val soldDate: LocalDate,
    val createdAt: LocalDateTime,
) {
    fun toDomain(): SoldRecord = SoldRecord(
        id = id,
        ticker = ticker,
        buyPrice = buyPrice,
        sellPrice = sellPrice,
        quantity = quantity,
        entryDate = entryDate,
        soldDate = soldDate,
        createdAt = createdAt,
    )

    companion object {
        fun fromDomain(r: SoldRecord): SoldRecordEntity = SoldRecordEntity(
            id = r.id,
            ticker = r.ticker,
            buyPrice = r.buyPrice,
            sellPrice = r.sellPrice,
            quantity = r.quantity,
            entryDate = r.entryDate,
            soldDate = r.soldDate,
            createdAt = r.createdAt,
        )
    }
}
