package com.babytigerdaddy.shfirstplayground.data.local

import android.content.Context
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeMode
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemePalette
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeShape
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** 사용자가 고른 테마(모양·색)를 기기에 저장. 다음 실행 때 그대로 복원. */
@Singleton
class ThemePreferenceStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)

    var shape: ThemeShape
        get() = runCatching { ThemeShape.valueOf(prefs.getString(KEY_SHAPE, null) ?: "") }
            .getOrDefault(ThemeShape.SOFT)
        set(value) { prefs.edit().putString(KEY_SHAPE, value.name).apply() }

    var palette: ThemePalette
        get() = runCatching { ThemePalette.valueOf(prefs.getString(KEY_PALETTE, null) ?: "") }
            .getOrDefault(ThemePalette.LIGHT)
        set(value) { prefs.edit().putString(KEY_PALETTE, value.name).apply() }

    // 화이트/다크 모드 선택. 저장값 없으면 SYSTEM(설치 직후 폰 설정을 따라감).
    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, null) ?: "") }
            .getOrDefault(ThemeMode.SYSTEM)
        set(value) { prefs.edit().putString(KEY_THEME_MODE, value.name).apply() }

    private companion object {
        const val KEY_SHAPE = "shape"
        const val KEY_PALETTE = "palette"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
