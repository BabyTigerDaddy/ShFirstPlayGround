package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.repository.JsonMicroCardRepository
import com.babytigerdaddy.shfirstplayground.data.repository.JsonRoutineRepository
import com.babytigerdaddy.shfirstplayground.data.repository.PersistentMemoRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.MemoRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.MicroCardRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.RoutineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 바인딩 — v3 Routine / MicroCard / Memo repositories.
 *
 * MemoRepository는 PersistentMemoRepository(DataStore-backed)로 영속.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(impl: JsonRoutineRepository): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindMicroCardRepository(impl: JsonMicroCardRepository): MicroCardRepository

    @Binds
    @Singleton
    abstract fun bindMemoRepository(impl: PersistentMemoRepository): MemoRepository
}
