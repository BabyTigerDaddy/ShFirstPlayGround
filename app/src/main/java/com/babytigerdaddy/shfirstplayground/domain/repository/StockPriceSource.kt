package com.babytigerdaddy.shfirstplayground.domain.repository

/**
 * 시세 소스 — 종목코드로 현재가를 가져온다.
 *
 * 야후파이낸스가 막히면 이 인터페이스 구현만 다른 무료 소스로 갈아끼우면 되게 감쌌다.
 * (화면·나머지 로직은 안 건드림.)
 */
interface StockPriceSource {
    /**
     * 종목의 현재가(원). 못 찾으면 null.
     *
     * @param code 6자리 종목코드, @param market "KOSPI"/"KOSDAQ" — 시장에 따라 심볼(.KS/.KQ)이 달라
     * 정확한 값을 받으려면 시장 구분이 필요하다(같은 코드가 .KS/.KQ에서 다른 종목일 수 있음).
     */
    suspend fun fetchPrice(code: String, market: String): Long?
}
