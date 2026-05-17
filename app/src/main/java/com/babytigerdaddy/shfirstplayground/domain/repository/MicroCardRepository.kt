package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.MicroCard
import java.time.LocalDate

/**
 * 마이크로카드 라이브러리 조회. v3 Phase 1은 정적 30장 사전 큐레이션, 일별 rotation.
 */
interface MicroCardRepository {

    /** 전체 카드 목록 (id 오름차순). */
    suspend fun listCards(): List<MicroCard>

    /** 주어진 날짜의 카드 1장 (deterministic — 같은 날짜는 같은 카드). */
    suspend fun cardForDate(date: LocalDate): MicroCard?
}
