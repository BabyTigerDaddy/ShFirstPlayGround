package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.MonthlyGoal
import kotlinx.coroutines.flow.Flow

/**
 * 월별 목표 저장·조회. v5 Room-backed.
 */
interface MonthlyGoalRepository {

    /** 전체 목표 관찰(yearMonth 오름차순). */
    fun observeAll(): Flow<List<MonthlyGoal>>

    /** 특정 달 목표(없으면 null). */
    suspend fun getByMonth(yearMonth: String): MonthlyGoal?

    /** 목표 저장 — 같은 달이면 덮어씀. */
    suspend fun save(goal: MonthlyGoal)
}
