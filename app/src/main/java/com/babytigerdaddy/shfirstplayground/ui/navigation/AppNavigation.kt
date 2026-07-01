package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.babytigerdaddy.shfirstplayground.ui.screen.holding.HoldingScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.settings.SettingsScreen

/**
 * holding-note-app root — '보유노트'(와이프용 보유현황 단독 앱).
 * 보유현황 화면 + 설정(테마) 화면을 톱니로 오간다.
 */
@Composable
fun AppNavigation() {
    var showSettings by rememberSaveable { mutableStateOf(false) }
    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
    } else {
        HoldingScreen(onOpenSettings = { showSettings = true })
    }
}
