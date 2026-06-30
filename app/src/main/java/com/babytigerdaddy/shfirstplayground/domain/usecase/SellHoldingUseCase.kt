package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.domain.repository.HoldingRepository
import com.babytigerdaddy.shfirstplayground.domain.repository.TradeJournalRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 보유 종목 매도 — '들고 있다 → 팔았다 → 일지에 남는다'를 한 흐름으로 잇는다.
 *
 * 1) 매도 종목의 실현손익([Holding.evalPnl])을 그날 매매일지에 더한다(같은 날이면 합산).
 * 2) 매도 종목명을 그날 일지 tickers에 합친다.
 * 3) 보유 목록에서 그 종목을 비운다.
 */
class SellHoldingUseCase @Inject constructor(
    private val holdingRepository: HoldingRepository,
    private val journalRepository: TradeJournalRepository,
) {
    /**
     * @param mood 매도 시 그날 기분(그날 첫 기록일 때만 반영, 기존 일지 있으면 유지).
     * @param note 매도 메모(그날 첫 기록일 때만 반영).
     * @param soldOn 매도일(기본 오늘).
     */
    suspend operator fun invoke(
        holding: Holding,
        mood: TradeMood = TradeMood.FLAT,
        note: String = "",
        soldOn: LocalDate = LocalDate.now(),
    ) {
        val realized = holding.evalPnl
        val now = LocalDateTime.now()
        val existing = journalRepository.getByDate(soldOn)

        val merged = if (existing != null) {
            // 그날 이미 일지가 있으면 손익·종목만 합산, 기분·메모는 기존 유지.
            existing.copy(
                realizedPnl = existing.realizedPnl + realized,
                tickers = (existing.tickers + holding.ticker).distinct(),
                updatedAt = now,
            )
        } else {
            TradeJournalEntry(
                date = soldOn,
                realizedPnl = realized,
                note = note,
                mood = mood,
                tickers = listOf(holding.ticker),
                createdAt = now,
                updatedAt = now,
            )
        }

        journalRepository.save(merged)
        holdingRepository.delete(holding.id)
    }
}
