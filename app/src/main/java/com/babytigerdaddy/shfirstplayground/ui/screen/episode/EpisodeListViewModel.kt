package com.babytigerdaddy.shfirstplayground.ui.screen.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.Episode
import com.babytigerdaddy.shfirstplayground.domain.repository.EpisodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EpisodeListUiState(
    val episodes: List<Episode> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class EpisodeListViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EpisodeListUiState())
    val uiState: StateFlow<EpisodeListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val list = episodeRepository.listEpisodes()
                _uiState.value = EpisodeListUiState(episodes = list, loading = false)
            } catch (e: Exception) {
                _uiState.value = EpisodeListUiState(
                    loading = false,
                    error = "에피소드를 불러오지 못했어요.",
                )
            }
        }
    }
}
