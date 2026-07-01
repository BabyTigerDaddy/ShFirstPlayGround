package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 종목 마스터 한 건 — 전체 상장 종목의 코드·이름 목록(로컬 DB 캐시).
 *
 * 앱 진입 시 목록을 받아 로컬에 담아두고, 종목 추가 시 이름으로 검색해 코드를 자동 매칭한다.
 * (사용자가 6자리 코드를 직접 외워 넣지 않게.)
 */
data class StockMaster(
    /** 6자리 종목코드 — PK. */
    val code: String,
    /** 종목명. */
    val name: String,
    /** 시장 — "KOSPI" / "KOSDAQ". 시세 API 시장 구분용. */
    val market: String,
)
