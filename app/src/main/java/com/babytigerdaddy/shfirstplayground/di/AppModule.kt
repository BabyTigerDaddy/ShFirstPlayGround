package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.generator.StubQuestionGenerator
import com.babytigerdaddy.shfirstplayground.data.repository.StubContentRepository
import com.babytigerdaddy.shfirstplayground.domain.generator.QuestionGenerator
import com.babytigerdaddy.shfirstplayground.domain.repository.ContentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩. 현재는 stub 구현 노출. 소보고가 실 구현 추가하면 여기서 갈아끼움.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: StubContentRepository): ContentRepository

    @Binds
    @Singleton
    abstract fun bindQuestionGenerator(impl: StubQuestionGenerator): QuestionGenerator
}
