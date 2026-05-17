package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.repository.JsonEpisodeRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.EpisodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩 — v2 EpisodeRepository → JsonEpisodeRepository.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(impl: JsonEpisodeRepository): EpisodeRepository
}
