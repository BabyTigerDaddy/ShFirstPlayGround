package com.babytigerdaddy.shfirstplayground.ui.screen.v4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.HappyLog
import com.babytigerdaddy.shfirstplayground.domain.model.Mood
import com.babytigerdaddy.shfirstplayground.domain.repository.HappyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class HomeTabUiState(
    val note: String = "",
    val mood: Mood = Mood.JOYFUL,
    val photoUris: List<String> = emptyList(),
    val saving: Boolean = false,
    val savedAt: LocalDateTime? = null,
)

@HiltViewModel
class HomeTabViewModel @Inject constructor(
    private val happyLogRepository: HappyLogRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeTabUiState())
    val uiState: StateFlow<HomeTabUiState> = _uiState.asStateFlow()

    fun onNoteChange(text: String) {
        _uiState.update { it.copy(note = text) }
    }

    fun onMoodChange(mood: Mood) {
        _uiState.update { it.copy(mood = mood) }
    }

    fun onPhotoAdded(uri: String) {
        _uiState.update { it.copy(photoUris = it.photoUris + uri) }
    }

    fun onPhotoRemoved(uri: String) {
        _uiState.update { it.copy(photoUris = it.photoUris - uri) }
    }

    /** 메모가 비어있어도 사진 또는 mood만으로 저장 가능. note·photo 둘 다 없으면 save X. */
    fun save() {
        val state = _uiState.value
        if (state.note.isBlank() && state.photoUris.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true) }
            val log = HappyLog(
                id = UUID.randomUUID().toString(),
                occurredAt = LocalDateTime.now(),
                note = state.note.trim(),
                mood = state.mood,
                location = null,
                photoUris = state.photoUris,
            )
            happyLogRepository.save(log)
            _uiState.value = HomeTabUiState(savedAt = LocalDateTime.now())
        }
    }
}
