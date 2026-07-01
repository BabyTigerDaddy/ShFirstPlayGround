package com.babytigerdaddy.shfirstplayground.data.repository

import com.babytigerdaddy.shfirstplayground.domain.model.StockMaster

/**
 * 종목 마스터 초기 시드 — 검색 동작 확인용 대표 종목(임시).
 *
 * 실제 전체 상장 종목 목록은 KIS 종목정보 파일(kospi/kosdaq_code)을 받아 대체할 예정.
 * 그 전까지 앱이 비어보이지 않게 대표 종목만 심어둔다.
 */
object StockSeed {
    private const val KOSPI = "KOSPI"
    private const val KOSDAQ = "KOSDAQ"

    val list: List<StockMaster> = listOf(
        StockMaster("005930", "삼성전자", KOSPI),
        StockMaster("000660", "SK하이닉스", KOSPI),
        StockMaster("373220", "LG에너지솔루션", KOSPI),
        StockMaster("207940", "삼성바이오로직스", KOSPI),
        StockMaster("005380", "현대차", KOSPI),
        StockMaster("000270", "기아", KOSPI),
        StockMaster("068270", "셀트리온", KOSPI),
        StockMaster("035420", "NAVER", KOSPI),
        StockMaster("035720", "카카오", KOSPI),
        StockMaster("066570", "LG전자", KOSPI),
        StockMaster("006400", "삼성SDI", KOSPI),
        StockMaster("009150", "삼성전기", KOSPI),
        StockMaster("051910", "LG화학", KOSPI),
        StockMaster("005490", "POSCO홀딩스", KOSPI),
        StockMaster("012330", "현대모비스", KOSPI),
        StockMaster("028260", "삼성물산", KOSPI),
        StockMaster("105560", "KB금융", KOSPI),
        StockMaster("055550", "신한지주", KOSPI),
        StockMaster("259960", "크래프톤", KOSPI),
        StockMaster("323410", "카카오뱅크", KOSPI),
        StockMaster("352820", "하이브", KOSPI),
        StockMaster("078340", "컴투스", KOSDAQ),
        StockMaster("063080", "컴투스홀딩스", KOSDAQ),
        StockMaster("194480", "데브시스터즈", KOSDAQ),
        StockMaster("293490", "카카오게임즈", KOSDAQ),
        StockMaster("263750", "펄어비스", KOSDAQ),
        StockMaster("247540", "에코프로비엠", KOSDAQ),
        StockMaster("086520", "에코프로", KOSDAQ),
        StockMaster("035900", "JYP Ent.", KOSDAQ),
    )
}
