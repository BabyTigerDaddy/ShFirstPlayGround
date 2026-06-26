package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.GoalProgress
import com.babytigerdaddy.shfirstplayground.domain.model.MonthlyGoal
import com.babytigerdaddy.shfirstplayground.domain.model.PnlSummary
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.model.TradeStats
import com.babytigerdaddy.shfirstplayground.domain.repository.MonthlyGoalRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import com.babytigerdaddy.shfirstplayground.domain.usecase.ObservePnlSummaryUseCase
import com.babytigerdaddy.shfirstplayground.domain.usecase.StatsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    /** 입력 중인 종목명(엔터/추가 전). */
    val tickerInput: String = "",
    /** 오늘 추가한 종목들. */
    val tickers: List<String> = emptyList(),
    val saving: Boolean = false,
    /** 방금 저장한 날(저장 완료 표시용). */
    val savedDate: LocalDate? = null,
) {
    val canSave: Boolean get() = amountText.any { it.isDigit() } && !saving
}

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val repository: TradeJournalRepository,
    private val goalRepository: MonthlyGoalRepository,
    observePnlSummary: ObservePnlSummaryUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow(TradeInputUiState())
    val input: StateFlow<TradeInputUiState> = _input.asStateFlow()

    /** 추이 요약 — 일지 저장될 때마다 자동 갱신. */
    val summary: StateFlow<PnlSummary> = observePnlSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PnlSummary.EMPTY)

    /** 통계 — 요일별 승률·종목 순위·평균·최대연속손실·최대낙폭. */
    val stats: StateFlow<TradeStats> = repository.observeAll()
        .map(StatsCalculator::compute)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TradeStats.EMPTY)

    /** 전체 일지(날짜 오름차순) — 회고 탭의 메모 타임라인·기분별 분석 화면 재료. */
    val entries: StateFlow<List<TradeJournalEntry>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** 이번 달 목표 달성률 — 목표 또는 일지 변할 때 갱신. */
    val goalProgress: StateFlow<GoalProgress> =
        combine(goalRepository.observeAll(), repository.observeAll()) { goals, entries ->
            val ym = currentYearMonth()
            val target = goals.firstOrNull { it.yearMonth == ym }?.target ?: 0L
            val current = entries.filter { yearMonthOf(it.date) == ym }.sumOf { it.realizedPnl }
            val ratio = if (target > 0) (current.toDouble() / target).coerceIn(0.0, 1.0) else 0.0
            GoalProgress(yearMonth = ym, target = target, current = current, ratio = ratio)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            GoalProgress(currentYearMonth(), 0, 0, 0.0),
        )

    fun onAmountChange(text: String) {
        val digits = text.filter { it.isDigit() }.take(12)
        _input.update { it.copy(amountText = digits, savedDate = null) }
    }

    fun onProfitToggle(isProfit: Boolean) {
        _input.update { it.copy(isProfit = isProfit) }
    }

    fun onMoodChange(mood: TradeMood) {
        _input.update { it.copy(mood = mood) }
    }

    fun onTickerInputChange(text: String) {
        _input.update { it.copy(tickerInput = text) }
    }

    /** 입력 중인 종목명을 칩으로 확정. 중복·공백 방어. */
    fun onAddTicker() {
        _input.update { state ->
            val t = state.tickerInput.trim()
            if (t.isEmpty() || state.tickers.any { it.equals(t, ignoreCase = true) }) {
                state.copy(tickerInput = "")
            } else {
                state.copy(tickers = state.tickers + t, tickerInput = "")
            }
        }
    }

    fun onRemoveTicker(ticker: String) {
        _input.update { it.copy(tickers = it.tickers - ticker) }
    }

    /** 오늘 매매 안 한 날 — 0원으로 저장(승률 분모에서 자동 제외). */
    fun saveRestDay() = persist(0L, TradeMood.FLAT, emptyList())

    /** 익절/손절 금액 저장. */
    fun save() {
        val state = _input.value
        val abs = state.amountText.filter { it.isDigit() }.toLongOrNull() ?: return
        val signed = if (state.isProfit) abs else -abs
        persist(signed, state.mood, state.tickers)
    }

    /** 이번 달 목표 설정. */
    fun setMonthlyGoal(target: Long) {
        viewModelScope.launch {
            goalRepository.save(MonthlyGoal(yearMonth = currentYearMonth(), target = target))
        }
    }

    private fun persist(realizedPnl: Long, mood: TradeMood, tickers: List<String>) {
        viewModelScope.launch {
            _input.update { it.copy(saving = true) }
            val today = LocalDate.now()
            val now = LocalDateTime.now()
            val createdAt = repository.getByDate(today)?.createdAt ?: now
            repository.save(
                TradeJournalEntry(
                    date = today,
                    realizedPnl = realizedPnl,
                    note = "",
                    mood = mood,
                    tickers = tickers,
                    createdAt = createdAt,
                    updatedAt = now,
                ),
            )
            _input.value = TradeInputUiState(savedDate = today)
        }
    }

    private fun currentYearMonth(): String = yearMonthOf(LocalDate.now())

    private fun yearMonthOf(date: LocalDate): String =
        "%04d-%02d".format(date.year, date.monthValue)
}
