package com.babytigerdaddy.shfirstplayground.ui.screen.v4

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.babytigerdaddy.shfirstplayground.domain.model.Mood
import com.babytigerdaddy.shfirstplayground.ui.theme.MoodCalm
import com.babytigerdaddy.shfirstplayground.ui.theme.MoodCozy
import com.babytigerdaddy.shfirstplayground.ui.theme.MoodExcited
import com.babytigerdaddy.shfirstplayground.ui.theme.MoodJoyful
import com.babytigerdaddy.shfirstplayground.ui.theme.MoodProud
import java.time.format.DateTimeFormatter

@Composable
fun HomeTab(viewModel: HomeTabViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoAdded(it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Greeting()

        NoteField(value = state.note, onChange = viewModel::onNoteChange)
        MoodPicker(selected = state.mood, onSelect = viewModel::onMoodChange)
        PhotoSection(
            photoUris = state.photoUris,
            onPick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            onRemove = viewModel::onPhotoRemoved,
        )
        Button(
            onClick = viewModel::save,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.saving && (state.note.isNotBlank() || state.photoUris.isNotEmpty()),
        ) {
            Text(text = if (state.saving) "저장 중..." else "행복한 순간 저장")
        }
        state.savedAt?.let {
            Text(
                text = "✓ ${it.format(DateTimeFormatter.ofPattern("HH:mm:ss"))} 저장됨",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun Greeting() {
    Column {
        Text(
            text = "안녕, 엄마",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "오늘 우리 아이의 작은 행복을\n기록해 보세요",
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
private fun NoteField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().height(120.dp),
        label = { Text("오늘 무엇이 행복했나요?") },
        placeholder = { Text("예: 처음으로 자전거를 혼자 탔어요!") },
    )
}

@Composable
private fun MoodPicker(selected: Mood, onSelect: (Mood) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "기분", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Mood.entries.forEach { mood ->
                FilterChip(
                    selected = mood == selected,
                    onClick = { onSelect(mood) },
                    label = { Text(mood.label()) },
                    leadingIcon = {
                        Surface(
                            modifier = Modifier.size(12.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = mood.color(),
                            content = {},
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(),
                )
            }
        }
    }
}

@Composable
private fun PhotoSection(
    photoUris: List<String>,
    onPick: () -> Unit,
    onRemove: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "사진", style = MaterialTheme.typography.titleSmall)
            OutlinedButton(onClick = onPick) { Text("+ 사진 추가") }
        }
        if (photoUris.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photoUris) { uri ->
                    Card(
                        modifier = Modifier.size(96.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    OutlinedButton(onClick = { onRemove(uri) }) { Text("×") }
                }
            }
        }
    }
}

internal fun Mood.color() = when (this) {
    Mood.JOYFUL -> MoodJoyful
    Mood.EXCITED -> MoodExcited
    Mood.CALM -> MoodCalm
    Mood.PROUD -> MoodProud
    Mood.COZY -> MoodCozy
}

internal fun Mood.label() = when (this) {
    Mood.JOYFUL -> "즐거움"
    Mood.EXCITED -> "신남"
    Mood.CALM -> "평온"
    Mood.PROUD -> "자랑"
    Mood.COZY -> "포근"
}
