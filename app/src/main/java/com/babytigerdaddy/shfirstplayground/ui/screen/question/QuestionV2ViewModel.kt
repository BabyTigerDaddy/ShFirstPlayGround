package com.babytigerdaddy.shfirstplayground.ui.screen.question

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.data.local.EpisodeStatsStore
import com.babytigerdaddy.shfirstplayground.domain.model.Episode
import com.babytigerdaddy.shfirstplayground.domain.model.EpisodeQuestion
import com.babytigerdaddy.shfirstplayground.domain.repository.EpisodeRepository
import com.babytigerdaddy.shfirstplayground.ui.navigation.QuestionV2Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionV2UiState(
    val episode: Episode? = null,
    val questions: List<EpisodeQuestion> = emptyList(),
    val expandedIds: Set<String> = emptySet(),
    val usedIds: Set<String> = emptySet(),
    val ratingSubmitted: Int? = null,
    val showRatingPrompt: Boolean = false,
    val loading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class QuestionV2ViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
    private val statsStore: EpisodeStatsStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionV2UiState())
    val uiState: StateFlow<QuestionV2UiState> = _uiState.asStateFlow()

    private val episodeId: String = savedStateHandle[QuestionV2Route.ARG_EPISODE_ID] ?: ""

    init {
        load()
        observePersistedStats()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = QuestionV2UiState(loading = true)
            try {
                val episode = episodeRepository.getEpisode(episodeId)
                if (episode == null) {
                    _uiState.value = QuestionV2UiState(loading = false, error = "에피소드를 찾을 수 없어요.")
                    return@launch
                }
                val questions = episodeRepository.getQuestions(episodeId)
                _uiState.value = _uiState.value.copy(
                    episode = episode,
                    questions = questions,
                    loading = false,
                    error = if (questions.isEmpty()) "이 에피소드에는 질문이 아직 없어요." else null,
                )
            } catch (e: Exception) {
                _uiState.value = QuestionV2UiState(
                    loading = false,
                    error = "질문을 불러오지 못했어요.",
                )
            }
        }
    }

    private fun observePersistedStats() {
        viewModelScope.launch {
            statsStore.observeStats(episodeId).collect { stats ->
                _uiState.update { current ->
                    current.copy(
                        usedIds = stats.questionsUsed,
                        ratingSubmitted = stats.usefulnessRating,
                    )
                }
            }
        }
    }

    /**
     * 카드 tap — expand toggle + used 마킹 (옵션 2). DataStore에도 영속.
     */
    fun onQuestionTap(questionId: String) {
        _uiState.update { current ->
            val expanded = if (questionId in current.expandedIds) {
                current.expandedIds - questionId
            } else {
                current.expandedIds + questionId
            }
            current.copy(expandedIds = expanded)
        }
        viewModelScope.launch {
            statsStore.markQuestionUsed(episodeId, questionId)
        }
    }

    /** 부모가 화면 하단 "도움 됐어요?" 버튼 tap 시 prompt 노출. */
    fun showRatingPrompt() {
        _uiState.update { it.copy(showRatingPrompt = true) }
    }

    fun dismissRatingPrompt() {
        _uiState.update { it.copy(showRatingPrompt = false) }
    }

    fun submitRating(rating: Int) {
        viewModelScope.launch {
            statsStore.setUsefulnessRating(episodeId, rating)
            _uiState.update { it.copy(showRatingPrompt = false) }
        }
    }
}
