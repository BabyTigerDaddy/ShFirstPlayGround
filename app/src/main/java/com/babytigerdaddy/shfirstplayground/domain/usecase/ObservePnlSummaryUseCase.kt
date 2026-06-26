package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.PnlSummary
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 일지 변화를 구독해 추이 요약([PnlSummary])을 실시간으로 흘려주는 use case.
 *
 * 추이 화면 ViewModel이 이걸 구독하면, 일지 하나 저장될 때마다 누적·승률·월별이 자동 갱신.
 */
class ObservePnlSummaryUseCase @Inject constructor(
    private val repository: TradeJournalRepository,
) {
    operator fun invoke(): Flow<PnlSummary> =
        repository.observeAll().map(PnlCalculator::compute)
}
