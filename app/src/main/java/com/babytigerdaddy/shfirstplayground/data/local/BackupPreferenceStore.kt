package com.babytigerdaddy.shfirstplayground.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** 백업 켜짐 여부 + 마지막 백업 시각(epoch millis, 0=없음)을 기기에 저장. */
@Singleton
class BackupPreferenceStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("backup", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) { prefs.edit().putBoolean(KEY_ENABLED, value).apply() }

    var lastBackupAt: Long
        get() = prefs.getLong(KEY_LAST_AT, 0L)
        set(value) { prefs.edit().putLong(KEY_LAST_AT, value).apply() }

    private companion object {
        const val KEY_ENABLED = "enabled"
        const val KEY_LAST_AT = "last_at"
    }
}
