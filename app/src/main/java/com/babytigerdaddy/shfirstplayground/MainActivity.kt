package com.babytigerdaddy.shfirstplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.babytigerdaddy.shfirstplayground.ui.navigation.AppNavigation
import com.babytigerdaddy.shfirstplayground.ui.theme.ShFirstPlayGroundTheme
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var themeController: ThemeController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 사용자가 세팅에서 고른 모양·색 테마를 실시간 적용.
            val settings by themeController.settings.collectAsState()
            ShFirstPlayGroundTheme(settings = settings) {
                AppNavigation()
            }
        }
    }
}
