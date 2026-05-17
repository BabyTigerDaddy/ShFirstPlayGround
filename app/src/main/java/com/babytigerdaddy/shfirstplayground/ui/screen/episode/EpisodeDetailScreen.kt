package com.babytigerdaddy.shfirstplayground.ui.screen.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.Episode

/**
 * Episode detail. 부모용 컨텍스트 + 핵심 주제 + 캐릭터 + attribution.
 * '시청 후 질문 받기' CTA로 QuestionV2Screen 진입.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(
    onBack: () -> Unit,
    onStartQuestions: (String) -> Unit,
) {
    val viewModel: EpisodeDetailViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.episode?.title ?: "에피소드") },
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
                state.loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                state.error != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                state.episode != null -> DetailContent(
                    episode = state.episode!!,
                    onStartQuestions = {
                        viewModel.onStartQuestions()
                        onStartQuestions(viewModel.episodeId)
                    },
                )
            }
        }
    }
}

@Composable
private fun DetailContent(episode: Episode, onStartQuestions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "${episode.seriesTitle} · S${episode.seasonNumber}E${episode.episodeNumber} · ${episode.durationMin}분",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = episode.title, style = MaterialTheme.typography.displaySmall)

        SectionTitle("이번 에피의 핵심 주제")
        Text(text = episode.coreTheme, style = MaterialTheme.typography.bodyLarge)

        SectionTitle("부모용 컨텍스트")
        Text(text = episode.parentContext, style = MaterialTheme.typography.bodyMedium)

        SectionTitle("등장 캐릭터")
        Text(
            text = episode.characters.joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
        )

        Button(
            onClick = onStartQuestions,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("시청 후 질문 받기")
        }

        Text(
            text = episode.attribution,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}
