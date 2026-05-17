package com.babytigerdaddy.shfirstplayground.ui.screen.v4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.HappyLog
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimelineUiState(
    val logs: List<HappyLog> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class TimelineTabViewModel @Inject constructor(
    private val happyLogRepository: HappyLogRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            happyLogRepository.observeAll().collect { logs ->
                _uiState.update { it.copy(logs = logs, loading = false) }
            }
        }
    }
}
