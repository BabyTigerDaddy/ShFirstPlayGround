package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import java.time.LocalDate
import java.time.LocalDateTime

/** Room Entity for Holding. id(UUID)가 PK. */
@Entity(tableName = "holding")
data class HoldingEntity(
    @PrimaryKey val id: String,
    // defaultValue = Account.DEFAULT_ID('default') — v6→v7 마이그레이션 ALTER 문과 일치.
    @ColumnInfo(defaultValue = "'default'") val accountId: String,
    // 종목코드 — v7→v8 마이그레이션 ALTER 문과 일치(기본 빈 문자열).
    @ColumnInfo(defaultValue = "''") val code: String,
    val ticker: String,
    val buyPrice: Long,
    val currentPrice: Long,
    val quantity: Int,
    val entryDate: LocalDate,
    val createdAt: LocalDateTime,
) {
    fun toDomain(): Holding = Holding(
        id = id,
        accountId = accountId,
        code = code,
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
            accountId = h.accountId,
            code = h.code,
            ticker = h.ticker,
            buyPrice = h.buyPrice,
            currentPrice = h.currentPrice,
            quantity = h.quantity,
            entryDate = h.entryDate,
            createdAt = h.createdAt,
        )
    }
}
