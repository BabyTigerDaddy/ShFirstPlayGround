package com.babytigerdaddy.shfirstplayground.ui.screen.holding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.model.Account
import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.HoldingSummary
import com.babytigerdaddy.shfirstplayground.data.repository.StockSeed
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.StockMaster
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.repository.AccountRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.StockMasterRepository
import com.babytigerdaddy.shfirstplayground.domain.usecase.AllocationCalculator
import com.babytigerdaddy.shfirstplayground.domain.usecase.HoldingCalculator
import com.babytigerdaddy.shfirstplayground.domain.usecase.MasterSyncResult
import com.babytigerdaddy.shfirstplayground.domain.usecase.RecordSaleUseCase
import com.babytigerdaddy.shfirstplayground.domain.usecase.RefreshPricesUseCase
import com.babytigerdaddy.shfirstplayground.domain.usecase.SoldRecordCalculator
import com.babytigerdaddy.shfirstplayground.domain.usecase.SyncStockMasterUseCase
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
    /** 종목코드(6자리) — 시세 자동조회용, 선택. */
    val codeText: String = "",
    val buyPriceText: String = "",
    val currentPriceText: String = "",
    val quantityText: String = "1",
    val entryDate: LocalDate = LocalDate.now(),
    val saving: Boolean = false,
) {
    // 현재가는 자동 시세로 채워지므로 입력 필수 아님(비면 매수가로 시작 → 0%, 갱신 시 실제값).
    val canSave: Boolean
        get() = ticker.isNotBlank() && buyPriceText.any { it.isDigit() } && !saving
}

/** 종목 목록 동기화 상태. */
data class MasterSyncState(
    val loading: Boolean = false,
    /** 로컬에 전종목이 아직 없어 처음 받는 중(‘종목 목록 준비 중’). false면 갱신 확인(짧음). */
    val firstLoad: Boolean = false,
    /** 방금 갱신된 종목 수(0이면 변화 없음/미실행). */
    val updatedCount: Int = 0,
    val failed: Boolean = false,
)

/** 시세 자동 갱신 상태. */
data class PriceRefreshState(
    val loading: Boolean = false,
    /** 마지막으로 시세를 받아온 시각(성공 시). */
    val lastUpdated: java.time.LocalDateTime? = null,
    /** 마지막 갱신에서 시세 받아온 종목 수. */
    val lastFetchedCount: Int = 0,
    /** 마지막 갱신이 실패했는지(네트워크·차단 등). */
    val failed: Boolean = false,
)

@HiltViewModel
class HoldingViewModel @Inject constructor(
    private val repository: HoldingRepository,
    private val soldRepository: SoldRecordRepository,
    private val accountRepository: AccountRepository,
    private val stockMasterRepository: StockMasterRepository,
    private val recordSale: RecordSaleUseCase,
    private val refreshPricesUseCase: RefreshPricesUseCase,
    private val syncStockMasterUseCase: SyncStockMasterUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow(HoldingInputUiState())
    val input: StateFlow<HoldingInputUiState> = _input.asStateFlow()

    /** 현재 보고 있는 계좌 id. */
    private val _selectedAccountId = MutableStateFlow(Account.DEFAULT_ID)
    val selectedAccountId: StateFlow<String> = _selectedAccountId.asStateFlow()

    /** 계좌 목록. */
    val accounts: StateFlow<List<Account>> = accountRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** 종목 검색 후보(자동완성). */
    private val _stockCandidates = MutableStateFlow<List<StockMaster>>(emptyList())
    val stockCandidates: StateFlow<List<StockMaster>> = _stockCandidates.asStateFlow()

    /** 시세 자동 갱신 상태 — 상단 '업데이트 중 / 방금 갱신 HH:mm'용. */
    private val _priceRefresh = MutableStateFlow(PriceRefreshState())
    val priceRefresh: StateFlow<PriceRefreshState> = _priceRefresh.asStateFlow()

    /** 종목 목록 동기화 상태 — 첫 진입 '준비 중' / 갱신된 날 '갱신 중'용. */
    private val _masterSync = MutableStateFlow(MasterSyncState())
    val masterSync: StateFlow<MasterSyncState> = _masterSync.asStateFlow()

    init {
        viewModelScope.launch {
            // 다중계좌 첫 실행: 계좌 없으면 기본 계좌 생성(기존 데이터가 여기로 이어붙음).
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
            // 오프라인·원격 실패 대비 최소 시드(전종목은 syncStockMaster가 원격에서 받아 덮어씀).
            if (stockMasterRepository.count() == 0) {
                stockMasterRepository.saveAll(StockSeed.list)
            }
        }
        // 앱 진입 시 전종목 목록 동기화(버전 바뀐 날만 실제 다운로드).
        syncStockMaster()
    }

    /**
     * 종목 목록 동기화 — 공개 주소에서 전종목 받아 DB 갱신(버전 같으면 스킵).
     * 첫 진입이면 '준비 중', 새 종목으로 바뀐 날이면 '갱신 중'으로 상태를 노출한다.
     */
    fun syncStockMaster() {
        if (_masterSync.value.loading) return
        viewModelScope.launch {
            val firstLoad = stockMasterRepository.count() <= StockSeed.list.size
            _masterSync.update { it.copy(loading = true, firstLoad = firstLoad, failed = false) }
            val result = runCatching { syncStockMasterUseCase() }.getOrNull()
            _masterSync.value = when (result) {
                is MasterSyncResult.Updated ->
                    MasterSyncState(loading = false, firstLoad = firstLoad, updatedCount = result.count)
                MasterSyncResult.NoChange ->
                    MasterSyncState(loading = false)
                else ->
                    MasterSyncState(loading = false, firstLoad = firstLoad, failed = true)
            }
        }
    }

    /**
     * 시세 자동 갱신 — 앱 진입/새로고침 시 호출. 종목코드 있는 보유 종목 현재가를 시세 소스에서 받아 갱신.
     * 이미 갱신 중이면 무시(중복 방지).
     */
    fun refreshPrices() {
        if (_priceRefresh.value.loading) return
        viewModelScope.launch {
            _priceRefresh.update { it.copy(loading = true) }
            val fetched = runCatching { refreshPricesUseCase() }.getOrNull()
            _priceRefresh.value = PriceRefreshState(
                loading = false,
                lastUpdated = if (fetched != null) LocalDateTime.now() else _priceRefresh.value.lastUpdated,
                lastFetchedCount = fetched ?: 0,
                failed = fetched == null,
            )
        }
    }

    /** 종목명/코드로 검색해 후보 갱신 — 종목 추가 시 이름 입력마다 호출. */
    fun searchStock(query: String) {
        viewModelScope.launch {
            _stockCandidates.value =
                if (query.isBlank()) emptyList() else stockMasterRepository.search(query)
        }
    }

    /** 후보에서 종목 선택 — 이름·코드 자동 채움, 후보 닫음. */
    fun selectStock(stock: StockMaster) {
        _input.update { it.copy(ticker = stock.name, codeText = stock.code) }
        _stockCandidates.value = emptyList()
    }

    /** 선택 계좌의 보유 종목만('전체' 선택 시 모든 계좌 합산). */
    private val accountHoldings = combine(repository.observeAll(), _selectedAccountId) { list, acc ->
        if (acc == Account.ALL_ID) list else list.filter { it.accountId == acc }
    }

    /** 보유 종목 집계(선택 계좌). */
    val summary: StateFlow<HoldingSummary> = accountHoldings
        .map(HoldingCalculator::compute)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HoldingSummary.EMPTY)

    /** 매도 내역 집계(선택 계좌). */
    val soldHistory: StateFlow<SoldHistorySummary> =
        combine(soldRepository.observeAll(), _selectedAccountId) { list, acc ->
            if (acc == Account.ALL_ID) list else list.filter { it.accountId == acc }
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
    fun onCodeChange(text: String) =
        _input.update { it.copy(codeText = text.filter { c -> c.isDigit() }.take(6)) }
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
        // 현재가 비면 매수가로 시작(0%) — 자동 시세가 곧 진짜 값으로 덮음.
        val current = s.currentPriceText.filter { it.isDigit() }.toLongOrNull() ?: buy
        val qty = s.quantityText.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
        viewModelScope.launch {
            _input.update { it.copy(saving = true) }
            // '전체' 보기 상태에서 추가하면 첫 실제 계좌에 담는다.
            val targetAccount = _selectedAccountId.value.takeUnless { it == Account.ALL_ID }
                ?: accounts.value.firstOrNull()?.id ?: Account.DEFAULT_ID
            repository.save(
                Holding(
                    id = UUID.randomUUID().toString(),
                    accountId = targetAccount,
                    code = s.codeText.trim(),
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
     * 보유 종목 통째로 수정 — 종목명·매수가·현재가·수량·편입일까지.
     * 오타 나도 지우고 새로 쓸 필요 없이 그 자리에서 고침. id·계좌·최초기록은 보존한 채 넘긴다.
     */
    fun updateHolding(edited: Holding) {
        viewModelScope.launch {
            repository.save(edited)
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
