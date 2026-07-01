package com.babytigerdaddy.shfirstplayground.ui.screen.holding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.HoldingSummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import com.babytigerdaddy.shfirstplayground.domain.usecase.HoldingCalculator
import com.babytigerdaddy.shfirstplayground.domain.usecase.RecordSaleUseCase
import com.babytigerdaddy.shfirstplayground.domain.usecase.SoldRecordCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/** 보유 종목 추가 입력 상태. */
data class HoldingInputUiState(
    val ticker: String = "",
    val buyPriceText: String = "",
    val currentPriceText: String = "",
    val quantityText: String = "1",
    val entryDate: LocalDate = LocalDate.now(),
    val saving: Boolean = false,
) {
    val canSave: Boolean
        get() = ticker.isNotBlank() &&
            buyPriceText.any { it.isDigit() } &&
            currentPriceText.any { it.isDigit() } &&
            !saving
}

@HiltViewModel
class HoldingViewModel @Inject constructor(
    private val repository: HoldingRepository,
    private val soldRepository: SoldRecordRepository,
    private val recordSale: RecordSaleUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow(HoldingInputUiState())
    val input: StateFlow<HoldingInputUiState> = _input.asStateFlow()

    /** 보유 종목 집계 — 종목 변할 때 자동 갱신. */
    val summary: StateFlow<HoldingSummary> = repository.observeAll()
        .map(HoldingCalculator::compute)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HoldingSummary.EMPTY)

    /** 매도 내역 집계 — 총 실현·평균 수익률·승률·누적 그래프. '판 내역' 탭용. */
    val soldHistory: StateFlow<SoldHistorySummary> = soldRepository.observeAll()
        .map(SoldRecordCalculator::compute)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SoldHistorySummary.EMPTY)

    fun onTickerChange(text: String) = _input.update { it.copy(ticker = text) }
    fun onBuyPriceChange(text: String) =
        _input.update { it.copy(buyPriceText = text.filter { c -> c.isDigit() }.take(12)) }
    fun onCurrentPriceChange(text: String) =
        _input.update { it.copy(currentPriceText = text.filter { c -> c.isDigit() }.take(12)) }
    fun onQuantityChange(text: String) =
        _input.update { it.copy(quantityText = text.filter { c -> c.isDigit() }.take(9)) }
    fun onEntryDateChange(date: LocalDate) = _input.update { it.copy(entryDate = date) }

    /** 새 보유 종목 추가. */
    fun addHolding() {
        val s = _input.value
        val buy = s.buyPriceText.filter { it.isDigit() }.toLongOrNull() ?: return
        val current = s.currentPriceText.filter { it.isDigit() }.toLongOrNull() ?: return
        val qty = s.quantityText.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
        viewModelScope.launch {
            _input.update { it.copy(saving = true) }
            repository.save(
                Holding(
                    id = UUID.randomUUID().toString(),
                    ticker = s.ticker.trim(),
                    buyPrice = buy,
                    currentPrice = current,
                    quantity = qty,
                    entryDate = s.entryDate,
                    createdAt = LocalDateTime.now(),
                ),
            )
            _input.value = HoldingInputUiState()
        }
    }

    /** 현재가만 갱신(시세 수동 업데이트). */
    fun updateCurrentPrice(holding: Holding, newPrice: Long) {
        viewModelScope.launch {
            repository.save(holding.copy(currentPrice = newPrice))
        }
    }

    /**
     * 매도 — 보유에서 빼고 '매도 내역'으로 이관한다.
     *
     * 매도가는 매도 시점 현재가. (mood·note는 화면 호환을 위해 받되 이 앱에선 사용하지 않음.)
     */
    fun sell(holding: Holding, mood: TradeMood = TradeMood.FLAT, note: String = "") {
        viewModelScope.launch {
            recordSale(holding)
        }
    }

    /** 보유 종목 삭제(매도 아님 — 잘못 입력 제거). */
    fun remove(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    /** 판 내역 삭제(잘못 기록 제거). */
    fun deleteSoldRecord(id: String) {
        viewModelScope.launch { soldRepository.delete(id) }
    }
}
