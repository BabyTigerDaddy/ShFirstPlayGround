package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
 * v5 매매일지 루트 — 입력 / 추이 2탭.
 *
 * 입력·추이 둘 다 hiltViewModel()로 같은 [TradeViewModel](Activity 스코프)을 공유하므로
 * 입력에서 저장하면 추이가 자동 갱신.
 */
@Composable
fun TradeMainScreen() {
    var selected by rememberSaveable { mutableStateOf(TradeTab.INPUT) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TradeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selected,
                        onClick = { selected = tab },
                        icon = { Text(text = tab.emoji, fontSize = 20.sp) },
                        label = { Text(text = tab.label) },
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
                TradeTab.INPUT -> InputScreen()
                TradeTab.TREND -> TrendScreen()
            }
        }
    }
}

private enum class TradeTab(val label: String, val emoji: String) {
    INPUT("입력", "✏️"),
    TREND("추이", "📈"),
}
