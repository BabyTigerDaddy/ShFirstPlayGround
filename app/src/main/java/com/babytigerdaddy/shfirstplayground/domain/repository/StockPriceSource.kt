package com.babytigerdaddy.shfirstplayground.domain.repository

/**
 * 시세 소스 — 종목코드로 현재가를 가져온다.
 *
 * 야후파이낸스가 막히면 이 인터페이스 구현만 다른 무료 소스로 갈아끼우면 되게 감쌌다.
 * (화면·나머지 로직은 안 건드림.)
 */
interface StockPriceSource {
    /** 6자리 종목코드의 현재가(원). 못 찾으면 null. */
    suspend fun fetchPrice(code: String): Long?
}
