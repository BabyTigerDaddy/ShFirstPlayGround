package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.StockMasterRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.StockPriceSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** 시세 갱신 결과 — 받아온 종목 수 + 자동 시세 '대상' 종목코드 집합. */
data class RefreshResult(
    val fetchedCount: Int,
    /**
     * 자동 시세 '대상' 종목코드들 — 코드가 있고 종목 목록에서 시장(.KS/.KQ)이 찾아지는 종목.
     * 이번 시세를 일시적으로 못 받아도 대상이면 포함(자동 종목이 순간 실패로 '수동'으로 뒤집히지 않게).
     * 여기 없으면 '수동'(ETF 등 코드/시장 없어 직접 입력해야 하는 것).
     */
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
            // 코드+시장이 확인된 종목은 '자동 대상' — 이번 시세를 못 받아도 자동으로 표시(순간 실패로 '수동' 뒤집힘 방지).
            autoCodes.add(h.code)
            val price = priceSource.fetchPrice(h.code, market) ?: continue
            fetched++
            if (price != h.currentPrice) {
                holdingRepository.save(h.copy(currentPrice = price))
            }
        }
        return RefreshResult(fetched, autoCodes)
    }
}
