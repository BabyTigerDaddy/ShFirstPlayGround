package com.babytigerdaddy.shfirstplayground.ui.screen.episode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 에피소드 list 화면. v2 prototype 단계엔 Bluey S1E1 한 개만 표시.
 * 본격 내용은 다음 commit에서 채움.
 */
@Composable
fun EpisodeListScreen(onEpisodeClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "EpisodeListScreen (placeholder)", style = MaterialTheme.typography.titleMedium)
    }
}
