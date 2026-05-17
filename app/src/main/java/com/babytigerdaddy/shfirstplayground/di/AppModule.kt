package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.generator.TemplateQuestionGenerator
import com.babytigerdaddy.shfirstplayground.data.repository.JsonContentRepository
import com.babytigerdaddy.shfirstplayground.data.repository.JsonEpisodeRepository
import com.babytigerdaddy.shfirstplayground.domain.generator.QuestionGenerator
import com.babytigerdaddy.shfirstplayground.domain.repository.ContentRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.EpisodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩.
 *
 * - v2 (active): EpisodeRepository → JsonEpisodeRepository
 * - v1 (legacy, UI rebuild 완료 후 제거 예정): ContentRepository, QuestionGenerator
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(impl: JsonEpisodeRepository): EpisodeRepository

    // ---- v1 legacy bindings (UI v2 완료 후 일괄 제거) ----

    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: JsonContentRepository): ContentRepository

    @Binds
    @Singleton
    abstract fun bindQuestionGenerator(impl: TemplateQuestionGenerator): QuestionGenerator
}
