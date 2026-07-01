package com.babytigerdaddy.shfirstplayground.domain.repository

import com.babytigerdaddy.shfirstplayground.domain.model.StockMaster

/** 종목 마스터 저장·검색. */
interface StockMasterRepository {

    /** 이름/코드로 검색(자동완성 후보). */
    suspend fun search(query: String): List<StockMaster>

    /** 저장된 종목 수(0이면 아직 목록 미로딩). */
    suspend fun count(): Int

    /** 목록 일괄 저장(마스터 로딩·갱신 시). */
    suspend fun saveAll(stocks: List<StockMaster>)
}
