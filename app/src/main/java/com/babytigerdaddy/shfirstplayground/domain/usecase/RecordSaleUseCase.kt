package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * 보유 종목 매도 — 판 수량만큼 '매도 내역'으로 이관한다.
 *
 * 매도가([sellPrice])와 수량([quantity])을 직접 정할 수 있다(기본=현재가·전량).
 * 일부만 팔면 보유는 남은 수량으로 줄고, 전량 팔면 보유에서 사라진다.
 */
class RecordSaleUseCase @Inject constructor(
    private val holdingRepository: HoldingRepository,
    private val soldRepository: SoldRecordRepository,
) {
    suspend operator fun invoke(
        holding: Holding,
        sellPrice: Long = holding.currentPrice,
        quantity: Int = holding.quantity,
        soldOn: LocalDate = LocalDate.now(),
    ) {
        val soldQty = quantity.coerceIn(1, holding.quantity)
        soldRepository.save(
            SoldRecord(
                // 일부 매도면 보유가 남아 id가 겹치므로 매도 기록은 항상 새 id.
                id = UUID.randomUUID().toString(),
                accountId = holding.accountId,
                ticker = holding.ticker,
                buyPrice = holding.buyPrice,
                sellPrice = sellPrice,
                quantity = soldQty,
                entryDate = holding.entryDate,
                soldDate = soldOn,
                createdAt = LocalDateTime.now(),
            ),
        )
        if (soldQty >= holding.quantity) {
            holdingRepository.delete(holding.id)          // 전량 매도 → 보유 제거
        } else {
            holdingRepository.save(holding.copy(quantity = holding.quantity - soldQty)) // 일부 매도 → 수량 차감
        }
    }
}
