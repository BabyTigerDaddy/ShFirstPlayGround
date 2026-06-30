package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import com.babytigerdaddy.shfirstplayground.ui.screen.holding.HoldingScreen

/**
 * holding-note-app root — '보유노트'(와이프용 보유현황 단독 앱).
 * 매매일지/육아 화면은 코드엔 남아있으나 진입점은 보유현황 하나만 띄운다(탭 없음).
 */
@Composable
fun AppNavigation() {
    HoldingScreen()
}
