package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.babytigerdaddy.shfirstplayground.domain.model.HappyLog
import com.babytigerdaddy.shfirstplayground.domain.model.Location
import com.babytigerdaddy.shfirstplayground.domain.model.Mood
import java.time.LocalDateTime

/**
 * Room Entity for HappyLog. Location은 3 nullable columns(lat/lng/label)로 flatten.
 */
@Entity(tableName = "happy_log")
data class HappyLogEntity(
    @PrimaryKey val id: String,
    val occurredAt: LocalDateTime,
    val note: String,
    val mood: Mood,
    val latitude: Double?,
    val longitude: Double?,
    val locationLabel: String?,
    val photoUris: List<String>,
) {
    fun toDomain(): HappyLog = HappyLog(
        id = id,
        occurredAt = occurredAt,
        note = note,
        mood = mood,
        location = if (latitude != null && longitude != null) {
            Location(latitude = latitude, longitude = longitude, label = locationLabel)
        } else null,
        photoUris = photoUris,
    )

    companion object {
        fun fromDomain(log: HappyLog): HappyLogEntity = HappyLogEntity(
            id = log.id,
            occurredAt = log.occurredAt,
            note = log.note,
            mood = log.mood,
            latitude = log.location?.latitude,
            longitude = log.location?.longitude,
            locationLabel = log.location?.label,
            photoUris = log.photoUris,
        )
    }
}
