package com.babytigerdaddy.shfirstplayground.ui.screen.episode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.Episode
import com.babytigerdaddy.shfirstplayground.ui.theme.Secondary

/**
 * 에피소드 list. v2 prototype은 Bluey S1E1 한 개만 표시.
 * 시즌·다른 콘텐츠 확장은 v2 검증 통과 후 phase 2.
 */
@Composable
fun EpisodeListScreen(onEpisodeClick: (String) -> Unit) {
    val viewModel: EpisodeListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "오늘 함께 볼 에피소드",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "선택해서 부모용 가이드와 질문 카드를 받아보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when {
            state.loading -> LoadingState()
            state.error != null -> ErrorState(state.error!!)
            state.episodes.isEmpty() -> EmptyState()
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.episodes, key = { it.id }) { episode ->
                    EpisodeCard(episode = episode, onClick = { onEpisodeClick(episode.id) })
                }
            }
        }
    }
}

@Composable
private fun EpisodeCard(episode: Episode, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "${episode.seriesTitle} · S${episode.seasonNumber}E${episode.episodeNumber}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = episode.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = episode.coreTheme,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "${episode.durationMin}분",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Secondary)
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "준비된 에피소드가 없어요", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
    }
}
