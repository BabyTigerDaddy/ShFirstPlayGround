package com.babytigerdaddy.shfirstplayground.ui.screen.v4

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.GrowthMilestone
import com.babytigerdaddy.shfirstplayground.domain.model.MilestoneKind
import java.time.format.DateTimeFormatter

@Composable
fun GrowthTab(viewModel: GrowthTabViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InputCard(
            kind = state.inputKind,
            value = state.inputValue,
            description = state.inputDescription,
            onKindChange = viewModel::onKindChange,
            onValueChange = viewModel::onValueChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onSave = viewModel::save,
        )
        Text(text = "지금까지 기록", style = MaterialTheme.typography.titleMedium)
        if (state.milestones.isEmpty()) {
            Text(
                text = "아직 기록이 없어요. 첫 마일스톤을 남겨 보세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.milestones, key = { it.id }) { m ->
                    MilestoneRow(m)
                }
            }
        }
    }
}

@Composable
private fun InputCard(
    kind: MilestoneKind,
    value: String,
    description: String,
    onKindChange: (MilestoneKind) -> Unit,
    onValueChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "새 마일스톤", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MilestoneKind.entries.forEach { k ->
                    FilterChip(
                        selected = k == kind,
                        onClick = { onKindChange(k) },
                        label = { Text(k.label()) },
                    )
                }
            }
            if (kind == MilestoneKind.HEIGHT || kind == MilestoneKind.WEIGHT) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(if (kind == MilestoneKind.HEIGHT) "키 (cm)" else "몸무게 (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = {
                    Text(
                        if (kind == MilestoneKind.EVENT) "이벤트 설명 (예: 처음 자전거 탔어요)"
                        else "메모 (선택)",
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) { Text("기록") }
        }
    }
}

@Composable
private fun MilestoneRow(m: GrowthMilestone) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${m.kind.label()} · ${m.recordedOn.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val primary = buildString {
                m.numericValue?.let { append("${it} ${m.kind.unit()}") }
                m.description?.let {
                    if (isNotEmpty()) append(" — ")
                    append(it)
                }
            }
            Text(text = primary, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun MilestoneKind.label() = when (this) {
    MilestoneKind.HEIGHT -> "키"
    MilestoneKind.WEIGHT -> "몸무게"
    MilestoneKind.EVENT -> "이벤트"
}

private fun MilestoneKind.unit() = when (this) {
    MilestoneKind.HEIGHT -> "cm"
    MilestoneKind.WEIGHT -> "kg"
    MilestoneKind.EVENT -> ""
}
