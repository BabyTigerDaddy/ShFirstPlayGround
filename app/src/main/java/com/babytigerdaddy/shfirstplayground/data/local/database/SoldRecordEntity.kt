package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import java.time.LocalDate
import java.time.LocalDateTime

/** Room Entity for SoldRecord. id(UUID)가 PK. */
@Entity(tableName = "sold_record")
data class SoldRecordEntity(
    @PrimaryKey val id: String,
    // defaultValue = Account.DEFAULT_ID('default') — v6→v7 마이그레이션 ALTER 문과 일치.
    @ColumnInfo(defaultValue = "'default'") val accountId: String,
    val ticker: String,
    val buyPrice: Long,
    val sellPrice: Long,
    val quantity: Int,
    val entryDate: LocalDate,
    val soldDate: LocalDate,
    val createdAt: LocalDateTime,
    // 실현손익 직접 보정값 — v9→v10 마이그레이션 ADD COLUMN과 일치(기본 null=자동계산).
    val realizedOverride: Long? = null,
) {
    fun toDomain(): SoldRecord = SoldRecord(
        id = id,
        accountId = accountId,
        ticker = ticker,
        buyPrice = buyPrice,
        sellPrice = sellPrice,
        quantity = quantity,
        entryDate = entryDate,
        soldDate = soldDate,
        createdAt = createdAt,
        realizedOverride = realizedOverride,
    )

    companion object {
        fun fromDomain(r: SoldRecord): SoldRecordEntity = SoldRecordEntity(
            id = r.id,
            accountId = r.accountId,
            ticker = r.ticker,
            buyPrice = r.buyPrice,
            sellPrice = r.sellPrice,
            quantity = r.quantity,
            entryDate = r.entryDate,
            soldDate = r.soldDate,
            createdAt = r.createdAt,
            realizedOverride = r.realizedOverride,
        )
    }
}
