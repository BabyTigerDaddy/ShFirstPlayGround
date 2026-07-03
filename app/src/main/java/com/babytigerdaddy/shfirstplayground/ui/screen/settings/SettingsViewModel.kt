package com.babytigerdaddy.shfirstplayground.ui.screen.settings

import androidx.lifecycle.ViewModel
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeController
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeMode
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemePalette
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeShape
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val controller: ThemeController,
) : ViewModel() {
    val settings = controller.settings

    fun setShape(shape: ThemeShape) = controller.setShape(shape)
    fun setPalette(palette: ThemePalette) = controller.setPalette(palette)
    fun setThemeMode(mode: ThemeMode) = controller.setThemeMode(mode)
}
