package com.babytigerdaddy.shfirstplayground.ui.screen.v4

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

/** 4 tab BottomNavigation root. */
@Composable
fun MainScreen() {
    var selected by rememberSaveable { mutableStateOf(Tab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Tab.entries.forEach { tab ->
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
            label = "v4-tab-switch",
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) { tab ->
            when (tab) {
                Tab.HOME -> HomeTab()
                Tab.TIMELINE -> TimelineTab()
                Tab.MAP -> MapTab()
                Tab.GROWTH -> GrowthTab()
            }
        }
    }
}

private enum class Tab(val label: String, val emoji: String) {
    HOME("홈", "🏠"),
    TIMELINE("타임라인", "📓"),
    MAP("지도", "🗺️"),
    GROWTH("성장", "🌱"),
}
