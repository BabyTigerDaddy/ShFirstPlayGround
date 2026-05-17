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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                else -> Content(
                    questions = state.questions,
                    expandedIds = state.expandedIds,
                    usedIds = state.usedIds,
                    ratingSubmitted = state.ratingSubmitted,
                    onTap = viewModel::onQuestionTap,
                    onAskRating = viewModel::showRatingPrompt,
                )
            }

            if (state.showRatingPrompt) {
                RatingPromptDialog(
                    onSubmit = viewModel::submitRating,
                    onDismiss = viewModel::dismissRatingPrompt,
                )
            }
        }
    }
}

@Composable
private fun Content(
    questions: List<EpisodeQuestion>,
    expandedIds: Set<String>,
    usedIds: Set<String>,
    ratingSubmitted: Int?,
    onTap: (String) -> Unit,
    onAskRating: () -> Unit,
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
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(questions, key = { it.id }) { q ->
                QuestionCard(
                    question = q,
                    expanded = q.id in expandedIds,
                    used = q.id in usedIds,
                    onClick = { onTap(q.id) },
                )
            }
        }
        if (ratingSubmitted != null) {
            Text(
                text = "도움 됨 점수 ${ratingSubmitted}/5 기록됨",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            OutlinedButton(
                onClick = onAskRating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("이 에피, 우리에게 도움 됐어요?")
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
private fun RatingPromptDialog(onSubmit: (Int) -> Unit, onDismiss: () -> Unit) {
    var rating by remember { mutableIntStateOf(3) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("도움 점수") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("이 에피와 질문이 아이와 대화에 얼마나 도움 됐어요?")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { value ->
                        val selected = value == rating
                        OutlinedButton(
                            onClick = { rating = value },
                            modifier = Modifier.weight(1f),
                            colors = if (selected) {
                                androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                )
                            } else {
                                androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                            },
                        ) {
                            Text(text = value.toString())
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating) }) { Text("저장") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
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
