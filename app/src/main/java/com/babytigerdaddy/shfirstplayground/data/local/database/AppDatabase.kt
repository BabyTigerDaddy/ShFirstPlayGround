package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * v4 Room Database.
 *
 * v1 → v2: GrowthMilestoneEntity 추가. 미배포 단계라 schema migration 코드 X
 * (DatabaseBuilder.fallbackToDestructiveMigration 적용).
 */
@Database(
    entities = [HappyLogEntity::class, GrowthMilestoneEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun happyLogDao(): HappyLogDao
    abstract fun growthMilestoneDao(): GrowthMilestoneDao
}
