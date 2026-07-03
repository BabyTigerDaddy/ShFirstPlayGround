package com.babytigerdaddy.shfirstplayground.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.usecase.RefreshPricesUseCase
import com.babytigerdaddy.shfirstplayground.domain.usecase.SyncStockMasterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 스플래시 초기화 단계 — 하단 상태 문구용. */
enum class SplashPhase { SYNCING_LIST, REFRESHING_PRICES, DONE }

/**
 * 스플래시(로딩 페이지) — 앱 켤 때 '매기' 로고를 보여주는 동안
 * 시간 걸리는 초기화(종목 목록 동기화 → 시세 갱신)를 끝내고 메인으로 넘긴다.
 *
 * 로고가 깜빡이지 않게 최소 노출 시간을 두고 초기화와 동시에 진행.
 * [phase]로 단계(목록/시세/완료)를 알려 스플래시 하단 문구를 살린다.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncStockMaster: SyncStockMasterUseCase,
    private val refreshPrices: RefreshPricesUseCase,
    private val holdingRepository: HoldingRepository,
) : ViewModel() {

    private val _phase = MutableStateFlow(SplashPhase.SYNCING_LIST)
    /** 초기화 단계. [SplashPhase.DONE]이면 메인으로 전환. */
    val phase: StateFlow<SplashPhase> = _phase.asStateFlow()

    init {
        viewModelScope.launch {
            // 보유 종목이 아예 없으면 받아올 시세가 없어 로고가 깜빡 지나가버린다 — 그때만 최소 노출을 준다.
            // 종목이 있으면 시세 받는 시간만큼만 보여주고, 강제 지연 없이 끝나는 대로 메인으로 넘어간다.
            val hasHoldings = runCatching { holdingRepository.observeAll().first().isNotEmpty() }
                .getOrDefault(false)
            val minShow = if (hasHoldings) null else launch { delay(EMPTY_MIN_SHOW_MS) }
            _phase.value = SplashPhase.SYNCING_LIST
            runCatching { syncStockMaster() }            // 종목 목록 동기화(무거운 작업)
            _phase.value = SplashPhase.REFRESHING_PRICES
            runCatching { refreshPrices() }              // 현재가 갱신
            minShow?.join()
            _phase.value = SplashPhase.DONE
        }
    }

    private companion object {
        // 보유 종목이 없을 때만 로고 최소 노출 0.5초(깜빡임 방지). 종목이 있으면 강제 지연 없음.
        const val EMPTY_MIN_SHOW_MS = 500L
    }
}
