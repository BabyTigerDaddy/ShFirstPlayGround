package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.PnlSummary
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import com.babytigerdaddy.shfirstplayground.domain.usecase.ObservePnlSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/** 입력 화면 상태. */
data class TradeInputUiState(
    /** 사용자가 친 금액(절대값, 숫자만). */
    val amountText: String = "",
    /** true=익절(+), false=손절(−). */
    val isProfit: Boolean = true,
    val mood: TradeMood = TradeMood.FLAT,
    val saving: Boolean = false,
    /** 방금 저장한 날(저장 완료 표시용). */
    val savedDate: LocalDate? = null,
) {
    val canSave: Boolean get() = amountText.any { it.isDigit() } && !saving
}

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val repository: TradeJournalRepository,
    observePnlSummary: ObservePnlSummaryUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow(TradeInputUiState())
    val input: StateFlow<TradeInputUiState> = _input.asStateFlow()

    /** 추이 요약 — 일지 저장될 때마다 자동 갱신. */
    val summary: StateFlow<PnlSummary> = observePnlSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PnlSummary.EMPTY)

    fun onAmountChange(text: String) {
        // 숫자만 남김(콤마·기타 입력 방어).
        val digits = text.filter { it.isDigit() }.take(12)
        _input.update { it.copy(amountText = digits, savedDate = null) }
    }

    fun onProfitToggle(isProfit: Boolean) {
        _input.update { it.copy(isProfit = isProfit) }
    }

    fun onMoodChange(mood: TradeMood) {
        _input.update { it.copy(mood = mood) }
    }

    /** 오늘 매매 안 한 날 — 0원으로 저장(승률 분모에서 자동 제외). */
    fun saveRestDay() = persist(0L, TradeMood.FLAT)

    /** 익절/손절 금액 저장. */
    fun save() {
        val state = _input.value
        val abs = state.amountText.filter { it.isDigit() }.toLongOrNull() ?: return
        val signed = if (state.isProfit) abs else -abs
        persist(signed, state.mood)
    }

    private fun persist(realizedPnl: Long, mood: TradeMood) {
        viewModelScope.launch {
            _input.update { it.copy(saving = true) }
            val today = LocalDate.now()
            val now = LocalDateTime.now()
            // 같은 날 기록 있으면 createdAt 보존.
            val createdAt = repository.getByDate(today)?.createdAt ?: now
            repository.save(
                TradeJournalEntry(
                    date = today,
                    realizedPnl = realizedPnl,
                    note = "",
                    mood = mood,
                    createdAt = createdAt,
                    updatedAt = now,
                ),
            )
            _input.value = TradeInputUiState(savedDate = today)
        }
    }
}
