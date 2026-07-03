package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDateTime

/**
 * 계좌 — 가치투자용·단타용처럼 목적별로 나눠 보는 단위.
 *
 * 보유([Holding])·매도내역([SoldRecord])이 각자 [Holding.accountId]로 소속 계좌를 가리킨다.
 * 다중계좌 업데이트 시 기존 데이터는 [DEFAULT_ID] 기본 계좌로 이어붙는다.
 */
data class Account(
    val id: String,
    val name: String,
    /** 계좌 탭 정렬 순서(작을수록 앞). */
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    /** 현금 잔액(원) — 자산 배분에 '현금 비중'으로 포함. 수익률 계산엔 미포함. */
    val cash: Long = 0,
) {
    companion object {
        /** 기존 데이터가 이어붙는 기본 계좌 id — 마이그레이션과 SQL default가 공유하는 값. */
        const val DEFAULT_ID = "default"
        const val DEFAULT_NAME = "내 계좌"

        /** 가상 '전체' 계좌 id — 선택 시 모든 계좌를 합쳐서 본다(실제 저장 계좌 아님). */
        const val ALL_ID = "__all__"
        const val ALL_NAME = "전체"
    }
}
