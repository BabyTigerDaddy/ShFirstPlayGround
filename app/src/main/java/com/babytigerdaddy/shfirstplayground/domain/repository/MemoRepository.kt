package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.Memo
import java.time.LocalDate

/**
 * 부모 메모 저장·조회. Phase 1은 in-memory(단일 디바이스), Phase 2에서 Room 영속.
 *
 * Success criteria (c) "메모 입력 평균 3회/주" 측정용 데이터 소스.
 */
interface MemoRepository {

    /** 주어진 날짜의 메모 (없으면 null). */
    suspend fun getMemoForDate(date: LocalDate): Memo?

    /** 메모 저장 (highlight 또는 challenge 둘 중 하나 이상 비어있지 않아야). */
    suspend fun saveMemo(memo: Memo)

    /** 기간 내 메모 수 (week 단위 success criteria 측정용). */
    suspend fun countMemosBetween(start: LocalDate, endInclusive: LocalDate): Int
}
