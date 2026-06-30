package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.SoldRecordRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 보유 종목 매도 — 보유에서 빼고 '매도 내역'으로 이관한다.
 *
 * 매도가는 매도 시점 현재가([Holding.currentPrice]). 보유노트 안에서
 * '들고 있다 → 팔았다'가 보유 목록 → 매도 내역으로 옮겨가는 흐름.
 */
class RecordSaleUseCase @Inject constructor(
    private val holdingRepository: HoldingRepository,
    private val soldRepository: SoldRecordRepository,
) {
    suspend operator fun invoke(holding: Holding, soldOn: LocalDate = LocalDate.now()) {
        soldRepository.save(
            SoldRecord(
                id = holding.id,
                ticker = holding.ticker,
                buyPrice = holding.buyPrice,
                sellPrice = holding.currentPrice,
                quantity = holding.quantity,
                entryDate = holding.entryDate,
                soldDate = soldOn,
                createdAt = LocalDateTime.now(),
            ),
        )
        holdingRepository.delete(holding.id)
    }
}
