package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.repository.InMemoryGrowthMilestoneRepository
import com.babytigerdaddy.shfirstplayground.data.repository.RoomHappyLogRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.GrowthMilestoneRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩 — v4 HappyLog (Room-backed) / GrowthMilestone (InMemory, 다음 cycle Room 교체).
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
        impl: InMemoryGrowthMilestoneRepository,
    ): GrowthMilestoneRepository
}
