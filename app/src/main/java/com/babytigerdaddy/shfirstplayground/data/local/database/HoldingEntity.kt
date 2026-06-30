package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import java.time.LocalDate
import java.time.LocalDateTime

/** Room Entity for Holding. id(UUID)가 PK. */
@Entity(tableName = "holding")
data class HoldingEntity(
    @PrimaryKey val id: String,
    val ticker: String,
    val buyPrice: Long,
    val currentPrice: Long,
    val quantity: Int,
    val entryDate: LocalDate,
    val createdAt: LocalDateTime,
) {
    fun toDomain(): Holding = Holding(
        id = id,
        ticker = ticker,
        buyPrice = buyPrice,
        currentPrice = currentPrice,
        quantity = quantity,
        entryDate = entryDate,
        createdAt = createdAt,
    )

    companion object {
        fun fromDomain(h: Holding): HoldingEntity = HoldingEntity(
            id = h.id,
            ticker = h.ticker,
            buyPrice = h.buyPrice,
            currentPrice = h.currentPrice,
            quantity = h.quantity,
            entryDate = h.entryDate,
            createdAt = h.createdAt,
        )
    }
}
