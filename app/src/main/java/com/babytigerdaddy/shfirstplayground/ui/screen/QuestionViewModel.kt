package com.babytigerdaddy.shfirstplayground.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.generator.QuestionGenerator
import com.babytigerdaddy.shfirstplayground.domain.model.Content
import com.babytigerdaddy.shfirstplayground.domain.model.Question
import com.babytigerdaddy.shfirstplayground.domain.repository.ContentRepository
import com.babytigerdaddy.shfirstplayground.ui.navigation.QuestionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionUiState(
    val content: Content? = null,
    val questions: List<Question> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val questionGenerator: QuestionGenerator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionUiState())
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()

    init {
        val contentId: String = savedStateHandle[QuestionRoute.ARG_CONTENT_ID] ?: ""
        load(contentId)
    }

    private fun load(contentId: String) {
        viewModelScope.launch {
            val content = contentRepository.getContent(contentId)
            val questions = content?.let { questionGenerator.generate(it, 3) } ?: emptyList()
            _uiState.value = QuestionUiState(content = content, questions = questions, loading = false)
        }
    }
}
