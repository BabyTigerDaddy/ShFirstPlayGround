package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.GrowthMilestone
import com.babytigerdaddy.shfirstplayground.domain.model.MilestoneKind
import java.time.LocalDate

@Entity(tableName = "growth_milestone")
data class GrowthMilestoneEntity(
    @PrimaryKey val id: String,
    val recordedOn: LocalDate,
    val kind: MilestoneKind,
    val numericValue: Double?,
    val description: String?,
) {
    fun toDomain(): GrowthMilestone = GrowthMilestone(
        id = id,
        recordedOn = recordedOn,
        kind = kind,
        numericValue = numericValue,
        description = description,
    )

    companion object {
        fun fromDomain(m: GrowthMilestone): GrowthMilestoneEntity = GrowthMilestoneEntity(
            id = m.id,
            recordedOn = m.recordedOn,
            kind = m.kind,
            numericValue = m.numericValue,
            description = m.description,
        )
    }
}
