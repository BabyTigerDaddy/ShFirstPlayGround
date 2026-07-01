package com.babytigerdaddy.shfirstplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.babytigerdaddy.shfirstplayground.ui.navigation.AppNavigation
import com.babytigerdaddy.shfirstplayground.ui.theme.ShFirstPlayGroundTheme
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeController
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemePalette
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var themeController: ThemeController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 모양은 사용자 선택 유지, 화이트/다크는 시스템 설정을 따라 자동 전환.
            val settings by themeController.settings.collectAsState()
            val effective = settings.copy(
                palette = if (isSystemInDarkTheme()) ThemePalette.DARK else ThemePalette.LIGHT,
            )
            ShFirstPlayGroundTheme(settings = effective) {
                AppNavigation()
            }
        }
    }
}
