package com.babytigerdaddy.shfirstplayground.di

import android.content.Context
import androidx.room.Room
import com.babytigerdaddy.shfirstplayground.data.local.database.AppDatabase
import com.babytigerdaddy.shfirstplayground.data.local.database.GrowthMilestoneDao
import com.babytigerdaddy.shfirstplayground.data.local.database.HappyLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Room DB 및 DAO Hilt 제공자.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration() // 미배포 단계라 schema 변경 시 데이터 wipe OK
            .build()

    @Provides
    fun provideHappyLogDao(database: AppDatabase): HappyLogDao = database.happyLogDao()

    @Provides
    fun provideGrowthMilestoneDao(database: AppDatabase): GrowthMilestoneDao =
        database.growthMilestoneDao()

    private const val DB_NAME = "shfirstplayground.db"
}
