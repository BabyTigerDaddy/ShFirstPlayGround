package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.MarketIndex

/**
 * 시장 지수(코스피·코스닥) 소스. 야후가 막히면 이 구현만 다른 무료 소스로 갈아끼우면 된다.
 */
interface MarketIndexSource {
    /** 코스피·코스닥 지수. 개별 실패 종목은 빠질 수 있고, 전부 실패면 빈 리스트. */
    suspend fun fetch(): List<MarketIndex>
}
