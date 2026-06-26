package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Room Entity for TradeJournalEntry. 하루 한 장이라 [date]가 PK.
 */
@Entity(tableName = "trade_journal")
data class TradeJournalEntity(
    @PrimaryKey val date: LocalDate,
    val realizedPnl: Long,
    val note: String,
    val mood: TradeMood,
    val tickers: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun toDomain(): TradeJournalEntry = TradeJournalEntry(
        date = date,
        realizedPnl = realizedPnl,
        note = note,
        mood = mood,
        tickers = tickers,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(entry: TradeJournalEntry): TradeJournalEntity = TradeJournalEntity(
            date = entry.date,
            realizedPnl = entry.realizedPnl,
            note = entry.note,
            mood = entry.mood,
            tickers = entry.tickers,
            createdAt = entry.createdAt,
            updatedAt = entry.updatedAt,
        )
    }
}
