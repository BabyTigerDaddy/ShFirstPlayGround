package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.StockMasterRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.StockPriceSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** 시세 갱신 결과 — 받아온 종목 수 + 자동 시세가 잡힌 종목코드 집합. */
data class RefreshResult(
    val fetchedCount: Int,
    /** 야후에서 시세가 자동으로 잡힌 종목코드들. 여기 없으면 '수동'(ETF 등 직접 입력). */
    val autoCodes: Set<String>,
)

/**
 * 시세 자동 갱신 — 종목코드가 있는 보유 종목의 현재가를 시세 소스에서 받아 갱신한다.
 *
 * 앱 진입/새로고침 시 호출. 저장하면 수익률·평가손익·배분이 자동으로 다시 계산된다.
 */
class RefreshPricesUseCase @Inject constructor(
    private val holdingRepository: HoldingRepository,
    private val stockMasterRepository: StockMasterRepository,
    private val priceSource: StockPriceSource,
) {
    /** @return 받아온 종목 수 + 자동 시세가 잡힌 종목코드 집합(그 외는 수동 대상). */
    suspend operator fun invoke(): RefreshResult {
        val holdings = holdingRepository.observeAll().first().filter { it.code.isNotBlank() }
        var fetched = 0
        val autoCodes = mutableSetOf<String>()
        for (h in holdings) {
            // 시장(.KS/.KQ)이 있어야 정확한 값 — 종목 마스터에서 판별. 없으면 건너뜀(엉뚱한 값 방지).
            val market = stockMasterRepository.getByCode(h.code)?.market ?: continue
            val price = priceSource.fetchPrice(h.code, market) ?: continue
            fetched++
            autoCodes.add(h.code)
            if (price != h.currentPrice) {
                holdingRepository.save(h.copy(currentPrice = price))
            }
        }
        return RefreshResult(fetched, autoCodes)
    }
}
