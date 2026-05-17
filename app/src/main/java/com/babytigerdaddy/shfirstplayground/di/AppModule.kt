package com.babytigerdaddy.shfirstplayground.di

import com.babytigerdaddy.shfirstplayground.data.repository.InMemoryMemoRepository
import com.babytigerdaddy.shfirstplayground.data.repository.JsonMicroCardRepository
import com.babytigerdaddy.shfirstplayground.data.repository.JsonRoutineRepository
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
 * InMemoryMemoRepository는 Phase 1 prototype용. 다음 cycle에 동생(sh-documents)이
 * DataStore-backed 구현으로 교체 예정.
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
    abstract fun bindMemoRepository(impl: InMemoryMemoRepository): MemoRepository
}
