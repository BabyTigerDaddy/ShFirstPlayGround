package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.ui.screen.holding.HoldingScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.splash.SplashPhase
import com.babytigerdaddy.shfirstplayground.ui.screen.splash.SplashScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.splash.SplashViewModel

/**
 * 매기(maegi) root — 앱 켜면 스플래시(로딩)로 시작해 초기화가 끝나면 보유현황 메인으로.
 * 보유 · 배분 · 판내역 · 설정은 HoldingScreen 안의 하단 탭바로 오간다.
 */
@Composable
fun AppNavigation() {
    val splashViewModel: SplashViewModel = hiltViewModel()
    val phase by splashViewModel.phase.collectAsStateWithLifecycle()
    if (phase != SplashPhase.DONE) {
        val statusText = when (phase) {
            SplashPhase.SYNCING_LIST -> "종목 목록 준비 중..."
            SplashPhase.REFRESHING_PRICES -> "시세 불러오는 중..."
            SplashPhase.DONE -> ""
        }
        SplashScreen(statusText = statusText)
    } else {
        HoldingScreen()
    }
}
