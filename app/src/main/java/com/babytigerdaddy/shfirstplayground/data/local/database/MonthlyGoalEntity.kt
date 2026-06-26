package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.MonthlyGoal

/** Room Entity for MonthlyGoal. yearMonth("YYYY-MM")가 PK. */
@Entity(tableName = "monthly_goal")
data class MonthlyGoalEntity(
    @PrimaryKey val yearMonth: String,
    val target: Long,
) {
    fun toDomain(): MonthlyGoal = MonthlyGoal(yearMonth = yearMonth, target = target)

    companion object {
        fun fromDomain(goal: MonthlyGoal): MonthlyGoalEntity =
            MonthlyGoalEntity(yearMonth = goal.yearMonth, target = goal.target)
    }
}
