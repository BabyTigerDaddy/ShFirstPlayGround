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
 */
@Database(
    entities = [
        HappyLogEntity::class,
        GrowthMilestoneEntity::class,
        TradeJournalEntity::class,
        MonthlyGoalEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun happyLogDao(): HappyLogDao
    abstract fun growthMilestoneDao(): GrowthMilestoneDao
    abstract fun tradeJournalDao(): TradeJournalDao
    abstract fun monthlyGoalDao(): MonthlyGoalDao
}
