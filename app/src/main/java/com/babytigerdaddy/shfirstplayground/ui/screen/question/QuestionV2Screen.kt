package com.babytigerdaddy.shfirstplayground.ui.screen.question

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.EpisodeQuestion
import com.babytigerdaddy.shfirstplayground.domain.model.QuestionCategory
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryApplication
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryCognition
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryComparison
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryEmotion
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryRecall

/**
 * Plot-aware 큐레이션 질문 카드 list.
 *
 * 카드 tap → expand context + used 마킹 (옵션 2). 5색 카테고리 차별화 v1 그대로 재사용.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionV2Screen(onBack: () -> Unit) {
    val viewModel: QuestionV2ViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.episode?.title ?: "질문") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.loading -> CenterMessage { CircularProgressIndicator() }
                state.error != null -> CenterMessage {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                }
                else -> QuestionList(
                    questions = state.questions,
                    expandedIds = state.expandedIds,
                    usedIds = state.usedIds,
                    onTap = viewModel::onQuestionTap,
                )
            }
        }
    }
}

@Composable
private fun QuestionList(
    questions: List<EpisodeQuestion>,
    expandedIds: Set<String>,
    usedIds: Set<String>,
    onTap: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "한 번씩 던져 보세요. 카드를 누르면 부모용 컨텍스트가 펼쳐져요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(questions, key = { it.id }) { q ->
                QuestionCard(
                    question = q,
                    expanded = q.id in expandedIds,
                    used = q.id in usedIds,
                    onClick = { onTap(q.id) },
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: EpisodeQuestion,
    expanded: Boolean,
    used: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = question.category.color()),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = question.category.label(), style = MaterialTheme.typography.labelMedium)
                if (used) {
                    Text(text = "✓ 사용함", style = MaterialTheme.typography.labelMedium)
                }
            }
            Text(text = question.text, style = MaterialTheme.typography.bodyLarge)
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "부모 컨텍스트: ${question.context}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterMessage(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

private fun QuestionCategory.color(): Color = when (this) {
    QuestionCategory.EMOTION -> CategoryEmotion
    QuestionCategory.COGNITION -> CategoryCognition
    QuestionCategory.RECALL -> CategoryRecall
    QuestionCategory.APPLICATION -> CategoryApplication
    QuestionCategory.COMPARISON -> CategoryComparison
}

private fun QuestionCategory.label(): String = when (this) {
    QuestionCategory.EMOTION -> "정서"
    QuestionCategory.COGNITION -> "인지"
    QuestionCategory.RECALL -> "회상"
    QuestionCategory.APPLICATION -> "적용"
    QuestionCategory.COMPARISON -> "비교"
}
