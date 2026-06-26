package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 하루치 매매일지 한 단위 — 단타 매매를 하는 아빠가 그날의 실현손익과 한 줄 일기를 남기는 엔트리.
 *
 * v5 핵심 도메인 모델. "하루 = 카드 하나" 원칙이라 [date]가 곧 ID(하루 한 장).
 * 같은 날 다시 저장하면 그날 카드를 덮어씀(upsert).
 */
data class TradeJournalEntry(
    /** 매매 날짜 — 하루 한 장이라 이 값이 곧 안정 ID. */
    val date: LocalDate,
    /** 그날의 실현손익(원). 이익이면 +, 손실이면 -, 매매 안 한 날은 0. */
    val realizedPnl: Long,
    /** 한 줄 일기 — "오늘 매매 어땠어요?" 자유 메모. */
    val note: String = "",
    /** 그날 매매 심리 라벨. */
    val mood: TradeMood = TradeMood.FLAT,
    /** 최초 기록 시점. */
    val createdAt: LocalDateTime,
    /** 마지막 수정 시점 — 같은 날 카드 덮어쓸 때 갱신. */
    val updatedAt: LocalDateTime,
) {
    /** 이익 난 날인지. 0(무매매)은 이익으로 안 침. */
    val isWin: Boolean get() = realizedPnl > 0

    /** 손실 난 날인지. */
    val isLoss: Boolean get() = realizedPnl < 0
}

/**
 * 그날 매매를 돌아보는 심리 라벨 5종 — 일지의 "일기" 성격을 살리는 회고 태그.
 *
 * 금액만 보면 안 보이는 "원칙을 지켰나"를 따로 남기려는 의도.
 */
enum class TradeMood {
    /** 원칙대로 — 계획한 대로 매매. 결과와 무관하게 잘 지킴. */
    DISCIPLINED,
    /** 만족 — 수익도 나고 기분도 좋음. */
    SATISFIED,
    /** 무덤덤 — 평범한 하루. */
    FLAT,
    /** 아쉬움 — 더 먹을 수 있었는데 / 손절이 늦었음. */
    REGRET,
    /** 과매매 — 욕심내서 너무 많이 사고팔았음(반성). */
    OVERTRADED,
}
