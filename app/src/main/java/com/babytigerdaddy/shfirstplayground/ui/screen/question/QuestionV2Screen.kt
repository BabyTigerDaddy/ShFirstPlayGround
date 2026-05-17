package com.babytigerdaddy.shfirstplayground.ui.screen.question

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * v2 질문 카드 화면. plot-aware 큐레이션 질문 5~7개 표시 (Bluey S1E1: 7개).
 * 본격 내용은 다음 commit에서 채움.
 */
@Composable
fun QuestionV2Screen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "QuestionV2Screen (placeholder)", style = MaterialTheme.typography.titleMedium)
    }
}
