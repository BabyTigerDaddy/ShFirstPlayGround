package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * v6 매매일지 루트 — 오늘 / 현황 / 달력 / 통계 / 회고 5탭.
 *
 * 모든 탭이 hiltViewModel()로 같은 [TradeViewModel](Activity 스코프)을 공유하므로
 * 입력에서 저장하면 나머지 탭이 자동 갱신.
 */
@Composable
fun TradeMainScreen() {
    var selected by rememberSaveable { mutableStateOf(TradeTab.TODAY) }

    Scaffold(
        containerColor = TradeBg,
        bottomBar = {
            NavigationBar(containerColor = TradeCard) {
                TradeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selected,
                        onClick = { selected = tab },
                        icon = { Text(text = tab.emoji, fontSize = 18.sp) },
                        label = { Text(text = tab.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TradeInk,
                            selectedTextColor = TradeInk,
                            indicatorColor = TradeBg,
                            unselectedIconColor = TradeMuted,
                            unselectedTextColor = TradeMuted,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = selected,
            label = "trade-tab-switch",
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) { tab ->
            when (tab) {
                TradeTab.TODAY -> InputScreen()
                TradeTab.DASHBOARD -> TrendScreen()
                TradeTab.CALENDAR -> CalendarScreen()
                TradeTab.STATS -> StatsScreen()
                TradeTab.RETRO -> RetroScreen()
            }
        }
    }
}

private enum class TradeTab(val label: String, val emoji: String) {
    TODAY("오늘", "✏️"),
    DASHBOARD("현황", "📈"),
    CALENDAR("달력", "📅"),
    STATS("통계", "📊"),
    RETRO("회고", "📝"),
}
