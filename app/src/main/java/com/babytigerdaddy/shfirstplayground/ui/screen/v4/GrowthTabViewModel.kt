package com.babytigerdaddy.shfirstplayground.ui.screen.v4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.GrowthMilestone
import com.babytigerdaddy.shfirstplayground.domain.model.MilestoneKind
import com.babytigerdaddy.shfirstplayground.domain.repository.GrowthMilestoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class GrowthTabUiState(
    val milestones: List<GrowthMilestone> = emptyList(),
    val loading: Boolean = true,
    val inputKind: MilestoneKind = MilestoneKind.HEIGHT,
    val inputValue: String = "",
    val inputDescription: String = "",
)

@HiltViewModel
class GrowthTabViewModel @Inject constructor(
    private val repository: GrowthMilestoneRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthTabUiState())
    val uiState: StateFlow<GrowthTabUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { list ->
                _uiState.update { it.copy(milestones = list, loading = false) }
            }
        }
    }

    fun onKindChange(kind: MilestoneKind) {
        _uiState.update { it.copy(inputKind = kind) }
    }

    fun onValueChange(text: String) {
        _uiState.update { it.copy(inputValue = text) }
    }

    fun onDescriptionChange(text: String) {
        _uiState.update { it.copy(inputDescription = text) }
    }

    /**
     * milestone 저장. HEIGHT·WEIGHT는 numeric value 필수, EVENT는 description 필수.
     */
    fun save() {
        val state = _uiState.value
        val numeric = state.inputValue.toDoubleOrNull()
        val desc = state.inputDescription.trim().takeIf { it.isNotBlank() }

        val isValid = when (state.inputKind) {
            MilestoneKind.HEIGHT, MilestoneKind.WEIGHT -> numeric != null && numeric > 0
            MilestoneKind.EVENT -> desc != null
        }
        if (!isValid) return

        viewModelScope.launch {
            repository.save(
                GrowthMilestone(
                    id = UUID.randomUUID().toString(),
                    recordedOn = LocalDate.now(),
                    kind = state.inputKind,
                    numericValue = numeric,
                    description = desc,
                ),
            )
            _uiState.update {
                it.copy(inputValue = "", inputDescription = "")
            }
        }
    }
}
