package com.babytigerdaddy.shfirstplayground.domain.usecase

import com.babytigerdaddy.shfirstplayground.data.local.MasterVersionStore
import com.babytigerdaddy.shfirstplayground.data.remote.StockMasterRemoteSource
import com.babytigerdaddy.shfirstplayground.domain.repository.StockMasterRepository
import javax.inject.Inject

/** 종목 마스터 동기화 결과. */
sealed interface MasterSyncResult {
    /** 원격 버전이 로컬과 같아 다시 안 받음. */
    data object NoChange : MasterSyncResult
    /** 새로 받아 [count]종목으로 갱신. */
    data class Updated(val count: Int) : MasterSyncResult
    /** 실패(네트워크 등). */
    data object Failed : MasterSyncResult
}

/**
 * 종목 마스터 동기화 — 공개 주소에서 목록을 받아 버전이 바뀌었을 때만 DB를 갱신.
 *
 * 앱 진입 시 호출. 버전 같으면 아무것도 안 받고 바로 끝(평소엔 조용).
 */
class SyncStockMasterUseCase @Inject constructor(
    private val remoteSource: StockMasterRemoteSource,
    private val repository: StockMasterRepository,
    private val versionStore: MasterVersionStore,
) {
    suspend operator fun invoke(): MasterSyncResult {
        val remote = remoteSource.fetchMaster() ?: return MasterSyncResult.Failed
        val sameVersion = remote.version == versionStore.version
        if (sameVersion && repository.count() > 0) return MasterSyncResult.NoChange

        repository.saveAll(remote.stocks)
        versionStore.version = remote.version
        return MasterSyncResult.Updated(remote.stocks.size)
    }
}
