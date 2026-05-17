package com.babytigerdaddy.shfirstplayground.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.Question
import com.babytigerdaddy.shfirstplayground.domain.model.QuestionCategory
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryApplication
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryCognition
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryComparison
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryEmotion
import com.babytigerdaddy.shfirstplayground.ui.theme.CategoryRecall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuestionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(state.content?.title ?: "질문") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "시청 후 아이에게 한 번씩 물어봐 주세요",
                style = MaterialTheme.typography.titleMedium,
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.questions, key = { it.id }) { question ->
                    QuestionCard(question = question)
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(question: Question) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = question.category.color()),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = question.category.label(),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
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
