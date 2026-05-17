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

data class MapTabUiState(
    val locatedLogs: List<HappyLog> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class MapTabViewModel @Inject constructor(
    private val happyLogRepository: HappyLogRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapTabUiState())
    val uiState: StateFlow<MapTabUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            happyLogRepository.observeAll().collect { logs ->
                _uiState.update {
                    it.copy(
                        locatedLogs = logs.filter { log -> log.location != null },
                        loading = false,
                    )
                }
            }
        }
    }
}
