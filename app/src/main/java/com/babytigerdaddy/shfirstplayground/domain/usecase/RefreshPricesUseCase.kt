package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.StockPriceSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 시세 자동 갱신 — 종목코드가 있는 보유 종목의 현재가를 시세 소스에서 받아 갱신한다.
 *
 * 앱 진입/새로고침 시 호출. 저장하면 수익률·평가손익·배분이 자동으로 다시 계산된다.
 */
class RefreshPricesUseCase @Inject constructor(
    private val holdingRepository: HoldingRepository,
    private val priceSource: StockPriceSource,
) {
    /** @return 시세를 받아온 종목 수(실패/코드없음 제외). */
    suspend operator fun invoke(): Int {
        val holdings = holdingRepository.observeAll().first().filter { it.code.isNotBlank() }
        var fetched = 0
        for (h in holdings) {
            val price = priceSource.fetchPrice(h.code) ?: continue
            fetched++
            if (price != h.currentPrice) {
                holdingRepository.save(h.copy(currentPrice = price))
            }
        }
        return fetched
    }
}
