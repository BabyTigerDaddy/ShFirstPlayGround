package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.generator.TemplateQuestionGenerator
import com.babytigerdaddy.shfirstplayground.data.repository.JsonContentRepository
import com.babytigerdaddy.shfirstplayground.domain.generator.QuestionGenerator
import com.babytigerdaddy.shfirstplayground.domain.repository.ContentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩. JSON-기반 실 구현(JsonContentRepository / TemplateQuestionGenerator)을 노출.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: JsonContentRepository): ContentRepository

    @Binds
    @Singleton
    abstract fun bindQuestionGenerator(impl: TemplateQuestionGenerator): QuestionGenerator
}
