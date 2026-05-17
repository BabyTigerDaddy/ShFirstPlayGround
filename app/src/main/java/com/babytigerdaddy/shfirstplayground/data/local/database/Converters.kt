package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.TypeConverter
import com.babytigerdaddy.shfirstplayground.domain.model.Mood
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room TypeConverters — v4 영속 모델 변환.
 *
 * - LocalDateTime ↔ ISO-8601 String
 * - List<String> ↔ csv String (사진 URI 다중)
 * - Mood enum ↔ String
 */
class Converters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? =
        value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }

    @TypeConverter
    fun fromStringList(value: List<String>?): String =
        value?.joinToString(SEP).orEmpty()

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        value?.split(SEP)?.filter { it.isNotEmpty() }.orEmpty()

    @TypeConverter
    fun fromMood(value: Mood?): String? = value?.name

    @TypeConverter
    fun toMood(value: String?): Mood? = value?.let { Mood.valueOf(it) }

    companion object {
        private const val SEP = "|"
    }
}
