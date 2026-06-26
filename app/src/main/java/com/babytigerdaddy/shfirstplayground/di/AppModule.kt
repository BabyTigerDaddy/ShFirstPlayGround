package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.repository.RoomGrowthMilestoneRepository
import com.babytigerdaddy.shfirstplayground.data.repository.RoomHappyLogRepository
import com.babytigerdaddy.shfirstplayground.data.repository.RoomMonthlyGoalRepository
import com.babytigerdaddy.shfirstplayground.data.repository.RoomTradeJournalRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.GrowthMilestoneRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.MonthlyGoalRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩 — v4 HappyLog · GrowthMilestone 둘 다 Room-backed.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindHappyLogRepository(impl: RoomHappyLogRepository): HappyLogRepository

    @Binds
    @Singleton
    abstract fun bindGrowthMilestoneRepository(
        impl: RoomGrowthMilestoneRepository,
    ): GrowthMilestoneRepository

    @Binds
    @Singleton
    abstract fun bindTradeJournalRepository(
        impl: RoomTradeJournalRepository,
    ): TradeJournalRepository

    @Binds
    @Singleton
    abstract fun bindMonthlyGoalRepository(
        impl: RoomMonthlyGoalRepository,
    ): MonthlyGoalRepository
}
