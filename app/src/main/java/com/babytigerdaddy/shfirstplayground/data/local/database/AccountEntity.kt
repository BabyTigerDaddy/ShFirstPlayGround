package com.babytigerdaddy.shfirstplayground.data.local.database

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
) {
    fun toDomain(): Account = Account(id = id, name = name, sortOrder = sortOrder, createdAt = createdAt)

    companion object {
        fun fromDomain(a: Account): AccountEntity =
            AccountEntity(id = a.id, name = a.name, sortOrder = a.sortOrder, createdAt = a.createdAt)
    }
}
