package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * v4 placeholder root. 다음 commit에서 동생(sh-documents)이 BottomNavigationBar + 4 탭 화면으로 교체.
 *
 * 4 탭 예정: 홈 / 타임라인 / 지도 / 성장.
 */
@Composable
fun AppNavigation() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "v4 준비 중\n곧 행복한 로그를 시작할 수 있어요",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
