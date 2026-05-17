package com.babytigerdaddy.shfirstplayground.ui.screen.v3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.Memo
import com.babytigerdaddy.shfirstplayground.domain.model.MicroCard
import com.babytigerdaddy.shfirstplayground.domain.model.Routine
import com.babytigerdaddy.shfirstplayground.domain.repository.MemoRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.MicroCardRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeV3UiState(
    val today: LocalDate = LocalDate.now(),
    val routines: List<Routine> = emptyList(),
    val checkedStepIds: Set<String> = emptySet(),
    val card: MicroCard? = null,
    val highlight: String = "",
    val challenge: String = "",
    val loading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeV3ViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val microCardRepository: MicroCardRepository,
    private val memoRepository: MemoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeV3UiState())
    val uiState: StateFlow<HomeV3UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val today = LocalDate.now()
            try {
                val routines = routineRepository.listRoutines()
                val card = microCardRepository.cardForDate(today)
                val memo = memoRepository.getMemoForDate(today)
                _uiState.value = HomeV3UiState(
                    today = today,
                    routines = routines,
                    card = card,
                    highlight = memo?.highlight.orEmpty(),
                    challenge = memo?.challenge.orEmpty(),
                    loading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "오늘 데이터를 불러오지 못했어요.",
                )
            }
        }
    }

    fun toggleStep(stepId: String) {
        _uiState.update { current ->
            val next = if (stepId in current.checkedStepIds) {
                current.checkedStepIds - stepId
            } else {
                current.checkedStepIds + stepId
            }
            current.copy(checkedStepIds = next)
        }
    }

    fun onHighlightChange(text: String) {
        _uiState.update { it.copy(highlight = text) }
    }

    fun onChallengeChange(text: String) {
        _uiState.update { it.copy(challenge = text) }
    }

    /**
     * 메모 저장 — highlight 또는 challenge 한 가지라도 비어있지 않으면 저장.
     * 둘 다 빈 문자열이면 save 호출 X (success criteria (c) 메모 수 측정에 노이즈 방지).
     */
    fun saveMemo() {
        val state = _uiState.value
        if (state.highlight.isBlank() && state.challenge.isBlank()) return
        viewModelScope.launch {
            memoRepository.saveMemo(
                Memo(
                    id = "memo-${state.today}",
                    date = state.today,
                    highlight = state.highlight.takeIf { it.isNotBlank() },
                    challenge = state.challenge.takeIf { it.isNotBlank() },
                ),
            )
        }
    }
}
