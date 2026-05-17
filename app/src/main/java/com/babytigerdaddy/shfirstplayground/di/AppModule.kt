package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.repository.InMemoryGrowthMilestoneRepository
import com.babytigerdaddy.shfirstplayground.data.repository.InMemoryHappyLogRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.GrowthMilestoneRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩 — v4 HappyLog / GrowthMilestone repositories.
 *
 * InMemory* 구현은 phase 1 prototype용. 다음 commit에서 Room-backed로 교체 예정.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindHappyLogRepository(impl: InMemoryHappyLogRepository): HappyLogRepository

    @Binds
    @Singleton
    abstract fun bindGrowthMilestoneRepository(
        impl: InMemoryGrowthMilestoneRepository,
    ): GrowthMilestoneRepository
}
