package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * v4 Room Database. Phase 1은 HappyLog 영속만, GrowthMilestone은 다음 cycle 추가.
 */
@Database(
    entities = [HappyLogEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun happyLogDao(): HappyLogDao
}
