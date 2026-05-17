package com.babytigerdaddy.shfirstplayground.ui.screen.v3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.MicroCard
import com.babytigerdaddy.shfirstplayground.domain.model.Routine

@Composable
fun HomeV3Screen(viewModel: HomeV3ViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.loading -> CenterMessage { CircularProgressIndicator() }
        state.error != null -> CenterMessage {
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
        }
        else -> HomeContent(
            state = state,
            onToggleStep = viewModel::toggleStep,
            onHighlightChange = viewModel::onHighlightChange,
            onChallengeChange = viewModel::onChallengeChange,
            onSaveMemo = viewModel::saveMemo,
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeV3UiState,
    onToggleStep: (String) -> Unit,
    onHighlightChange: (String) -> Unit,
    onChallengeChange: (String) -> Unit,
    onSaveMemo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Header(dateText = state.today.toString())
        state.card?.let { MicroCardSection(card = it) }
        state.routines.forEach { routine ->
            RoutineSection(
                routine = routine,
                checkedStepIds = state.checkedStepIds,
                onToggleStep = onToggleStep,
            )
        }
        MemoSection(
            highlight = state.highlight,
            challenge = state.challenge,
            onHighlightChange = onHighlightChange,
            onChallengeChange = onChallengeChange,
            onSaveMemo = onSaveMemo,
        )
    }
}

@Composable
private fun Header(dateText: String) {
    Column {
        Text(
            text = dateText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = "은하와 함께", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun MicroCardSection(card: MicroCard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "오늘의 카드", style = MaterialTheme.typography.labelMedium)
            Text(text = card.title, style = MaterialTheme.typography.titleLarge)
            Text(text = card.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun RoutineSection(
    routine: Routine,
    checkedStepIds: Set<String>,
    onToggleStep: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = routine.title, style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                routine.steps.forEach { step ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = step.id in checkedStepIds,
                            onCheckedChange = { onToggleStep(step.id) },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = step.title, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoSection(
    highlight: String,
    challenge: String,
    onHighlightChange: (String) -> Unit,
    onChallengeChange: (String) -> Unit,
    onSaveMemo: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "오늘의 한 줄 메모", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = highlight,
            onValueChange = onHighlightChange,
            label = { Text("잘한 점") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = challenge,
            onValueChange = onChallengeChange,
            label = { Text("도전한 점") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onSaveMemo) { Text("저장") }
        }
    }
}

@Composable
private fun CenterMessage(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}
