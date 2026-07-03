package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.Account
import java.time.LocalDateTime

/** Room Entity for Account. */
@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    // 현금 잔액 — v10→v11 마이그레이션 ALTER 문과 일치(기본 0).
    @ColumnInfo(defaultValue = "0") val cash: Long = 0,
) {
    fun toDomain(): Account = Account(id = id, name = name, sortOrder = sortOrder, createdAt = createdAt, cash = cash)

    companion object {
        fun fromDomain(a: Account): AccountEntity =
            AccountEntity(id = a.id, name = a.name, sortOrder = a.sortOrder, createdAt = a.createdAt, cash = a.cash)
    }
}
