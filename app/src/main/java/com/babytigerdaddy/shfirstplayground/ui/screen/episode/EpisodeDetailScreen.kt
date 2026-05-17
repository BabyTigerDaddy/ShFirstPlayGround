package com.babytigerdaddy.shfirstplayground.ui.screen.episode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Episode detail 화면. 제목·summary·캐릭터·attribution + 시청 후 질문 시작 CTA.
 * 본격 내용은 다음 commit에서 채움.
 */
@Composable
fun EpisodeDetailScreen(onBack: () -> Unit, onStartQuestions: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "EpisodeDetailScreen (placeholder)", style = MaterialTheme.typography.titleMedium)
    }
}
