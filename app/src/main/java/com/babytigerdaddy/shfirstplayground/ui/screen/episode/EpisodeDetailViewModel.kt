package com.babytigerdaddy.shfirstplayground.ui.screen.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.Episode
import com.babytigerdaddy.shfirstplayground.domain.repository.EpisodeRepository
import com.babytigerdaddy.shfirstplayground.ui.navigation.EpisodeDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EpisodeDetailUiState(
    val episode: Episode? = null,
    val loading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class EpisodeDetailViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EpisodeDetailUiState())
    val uiState: StateFlow<EpisodeDetailUiState> = _uiState.asStateFlow()

    val episodeId: String = savedStateHandle[EpisodeDetailRoute.ARG_EPISODE_ID] ?: ""

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = EpisodeDetailUiState(loading = true)
            try {
                val episode = episodeRepository.getEpisode(episodeId)
                _uiState.value = EpisodeDetailUiState(
                    episode = episode,
                    loading = false,
                    error = if (episode == null) "에피소드를 찾을 수 없어요." else null,
                )
            } catch (e: Exception) {
                _uiState.value = EpisodeDetailUiState(
                    loading = false,
                    error = "에피소드를 불러오지 못했어요.",
                )
            }
        }
    }
}
