package com.babytigerdaddy.shfirstplayground.di

import android.content.Context
import androidx.room.Room
import com.babytigerdaddy.shfirstplayground.data.local.database.AccountDao
import com.babytigerdaddy.shfirstplayground.data.local.database.AppDatabase
import com.babytigerdaddy.shfirstplayground.data.local.database.GrowthMilestoneDao
import com.babytigerdaddy.shfirstplayground.data.local.database.HappyLogDao
import com.babytigerdaddy.shfirstplayground.data.local.database.HoldingDao
import com.babytigerdaddy.shfirstplayground.data.local.database.MIGRATION_6_7
import com.babytigerdaddy.shfirstplayground.data.local.database.MIGRATION_7_8
import com.babytigerdaddy.shfirstplayground.data.local.database.MIGRATION_8_9
import com.babytigerdaddy.shfirstplayground.data.local.database.StockMasterDao
import com.babytigerdaddy.shfirstplayground.data.local.database.MonthlyGoalDao
import com.babytigerdaddy.shfirstplayground.data.local.database.SoldRecordDao
import com.babytigerdaddy.shfirstplayground.data.local.database.TradeJournalDao
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
            // 다중계좌(6→7)·종목코드(7→8) — 기존 데이터 보존 마이그레이션. 그 외 버전은 미배포라 wipe OK.
            .addMigrations(MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHappyLogDao(database: AppDatabase): HappyLogDao = database.happyLogDao()

    @Provides
    fun provideGrowthMilestoneDao(database: AppDatabase): GrowthMilestoneDao =
        database.growthMilestoneDao()

    @Provides
    fun provideTradeJournalDao(database: AppDatabase): TradeJournalDao =
        database.tradeJournalDao()

    @Provides
    fun provideMonthlyGoalDao(database: AppDatabase): MonthlyGoalDao =
        database.monthlyGoalDao()

    @Provides
    fun provideHoldingDao(database: AppDatabase): HoldingDao =
        database.holdingDao()

    @Provides
    fun provideSoldRecordDao(database: AppDatabase): SoldRecordDao =
        database.soldRecordDao()

    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDao =
        database.accountDao()

    @Provides
    fun provideStockMasterDao(database: AppDatabase): StockMasterDao =
        database.stockMasterDao()

    private const val DB_NAME = "shfirstplayground.db"
}
