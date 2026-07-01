package com.babytigerdaddy.shfirstplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.babytigerdaddy.shfirstplayground.ui.navigation.AppNavigation
import com.babytigerdaddy.shfirstplayground.ui.theme.ShFirstPlayGroundTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 보유노트는 와이프용 — 라이트 톤 고정(다크모드에서 다이얼로그 검정배경+검정글씨 방지)
            ShFirstPlayGroundTheme(darkTheme = false) {
                AppNavigation()
            }
        }
    }
}
