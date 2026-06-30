package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * v4 Room Database.
 *
 * v1 → v2: GrowthMilestoneEntity 추가. 미배포 단계라 schema migration 코드 X
 * (DatabaseBuilder.fallbackToDestructiveMigration 적용).
 * v2 → v3: TradeJournalEntity 추가 (v5 매매일지).
 * v3 → v4: TradeJournal에 tickers(종목) 컬럼 + MonthlyGoalEntity 추가 (상용화 확장).
 * v4 → v5: HoldingEntity 추가 (보유 종목 추적 — 와이프 요청).
 */
@Database(
    entities = [
        HappyLogEntity::class,
        GrowthMilestoneEntity::class,
        TradeJournalEntity::class,
        MonthlyGoalEntity::class,
        HoldingEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun happyLogDao(): HappyLogDao
    abstract fun growthMilestoneDao(): GrowthMilestoneDao
    abstract fun tradeJournalDao(): TradeJournalDao
    abstract fun monthlyGoalDao(): MonthlyGoalDao
    abstract fun holdingDao(): HoldingDao
}
