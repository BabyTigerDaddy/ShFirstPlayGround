package com.babytigerdaddy.shfirstplayground.ui.screen.holding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.Account
import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.HoldingSummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.repository.AccountRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import com.babytigerdaddy.shfirstplayground.domain.usecase.AllocationCalculator
import com.babytigerdaddy.shfirstplayground.domain.usecase.HoldingCalculator
import com.babytigerdaddy.shfirstplayground.domain.usecase.RecordSaleUseCase
import com.babytigerdaddy.shfirstplayground.domain.usecase.SoldRecordCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private val accountRepository: AccountRepository,
    private val recordSale: RecordSaleUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow(HoldingInputUiState())
    val input: StateFlow<HoldingInputUiState> = _input.asStateFlow()

    /** 현재 보고 있는 계좌 id. */
    private val _selectedAccountId = MutableStateFlow(Account.DEFAULT_ID)
    val selectedAccountId: StateFlow<String> = _selectedAccountId.asStateFlow()

    /** 계좌 목록. */
    val accounts: StateFlow<List<Account>> = accountRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // 다중계좌 업데이트 첫 실행: 계좌가 하나도 없으면 기본 계좌 생성(기존 데이터가 여기로 이어붙음).
        viewModelScope.launch {
            if (accountRepository.count() == 0) {
                accountRepository.save(
                    Account(
                        id = Account.DEFAULT_ID,
                        name = Account.DEFAULT_NAME,
                        sortOrder = 0,
                        createdAt = LocalDateTime.now(),
                    ),
                )
            }
        }
    }

    /** 선택 계좌의 보유 종목만. */
    private val accountHoldings = combine(repository.observeAll(), _selectedAccountId) { list, acc ->
        list.filter { it.accountId == acc }
    }

    /** 보유 종목 집계(선택 계좌). */
    val summary: StateFlow<HoldingSummary> = accountHoldings
        .map(HoldingCalculator::compute)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HoldingSummary.EMPTY)

    /** 매도 내역 집계(선택 계좌). */
    val soldHistory: StateFlow<SoldHistorySummary> =
        combine(soldRepository.observeAll(), _selectedAccountId) { list, acc ->
            list.filter { it.accountId == acc }
        }
            .map(SoldRecordCalculator::compute)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SoldHistorySummary.EMPTY)

    /** 자산 배분(선택 계좌). */
    val allocation: StateFlow<AssetAllocation> = accountHoldings
        .map(AllocationCalculator::compute)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AssetAllocation.EMPTY)

    // ---------- 계좌 ----------

    fun selectAccount(id: String) {
        _selectedAccountId.value = id
    }

    /** 새 계좌 추가하고 그 계좌로 전환. */
    fun addAccount(name: String) {
        val clean = name.trim().ifBlank { "새 계좌" }
        viewModelScope.launch {
            val order = (accounts.value.maxOfOrNull { it.sortOrder } ?: -1) + 1
            val id = UUID.randomUUID().toString()
            accountRepository.save(Account(id = id, name = clean, sortOrder = order, createdAt = LocalDateTime.now()))
            _selectedAccountId.value = id
        }
    }

    /** 계좌 이름 수정. */
    fun renameAccount(id: String, name: String) {
        val clean = name.trim().ifBlank { return }
        viewModelScope.launch {
            val acc = accounts.value.firstOrNull { it.id == id } ?: return@launch
            accountRepository.save(acc.copy(name = clean))
        }
    }

    /** 계좌 삭제 — 그 계좌의 보유·매도내역도 함께 정리. 마지막 한 개는 못 지움. */
    fun deleteAccount(id: String) {
        viewModelScope.launch {
            if (accountRepository.count() <= 1) return@launch
            repository.observeAll().first().filter { it.accountId == id }.forEach { repository.delete(it.id) }
            soldRepository.observeAll().first().filter { it.accountId == id }.forEach { soldRepository.delete(it.id) }
            accountRepository.delete(id)
            if (_selectedAccountId.value == id) {
                _selectedAccountId.value = accountRepository.observeAll().first().firstOrNull()?.id ?: Account.DEFAULT_ID
            }
        }
    }

    // ---------- 입력 ----------

    fun onTickerChange(text: String) = _input.update { it.copy(ticker = text) }
    fun onBuyPriceChange(text: String) =
        _input.update { it.copy(buyPriceText = text.filter { c -> c.isDigit() }.take(12)) }
    fun onCurrentPriceChange(text: String) =
        _input.update { it.copy(currentPriceText = text.filter { c -> c.isDigit() }.take(12)) }
    fun onQuantityChange(text: String) =
        _input.update { it.copy(quantityText = text.filter { c -> c.isDigit() }.take(9)) }
    fun onEntryDateChange(date: LocalDate) = _input.update { it.copy(entryDate = date) }

    /** 새 보유 종목 추가(현재 선택 계좌에). */
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
                    accountId = _selectedAccountId.value,
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
     * 매도 — 보유에서 빼고 '매도 내역'으로 이관한다(같은 계좌 유지).
     *
     * (mood·note는 화면 호환을 위해 받되 이 앱에선 사용하지 않음.)
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
