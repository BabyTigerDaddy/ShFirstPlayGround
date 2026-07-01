package com.babytigerdaddy.shfirstplayground.ui.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.data.remote.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 클라우드 백업 화면 상태 — 정책은 '로컬 우선'.
 * 켜면 즉시 동기화(새 폰이면 자동 복원, 아니면 업로드),
 * 양쪽 다 있을 땐 자동으로 안 덮고 '불러오기' 버튼 + 확인을 거쳐야만 덮어씀.
 */
@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
) : ViewModel() {
    var enabled by mutableStateOf(backupRepository.enabled)
        private set
    var syncing by mutableStateOf(false)
        private set
    var lastBackupAt by mutableStateOf(backupRepository.lastBackupAt)
        private set
    var hasCloudBackup by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    /** 로그인 상태 바뀔 때 클라우드에 백업 있는지 갱신 — '불러오기' 버튼 노출 판단용. */
    fun refreshCloudState() {
        viewModelScope.launch { hasCloudBackup = backupRepository.hasCloudBackup() }
    }

    /** 백업 스위치 — 켜면 즉시 동기화. 실패하면 스위치 되돌리고 이유 노출. */
    fun toggle(value: Boolean) {
        if (syncing) return
        if (!value) {
            backupRepository.disable()
            enabled = false
            message = null
            return
        }
        syncing = true
        message = null
        viewModelScope.launch {
            backupRepository.enableAndSync()
                .onSuccess {
                    enabled = true
                    lastBackupAt = backupRepository.lastBackupAt
                    hasCloudBackup = true
                    message = "백업이 켜졌어요"
                }
                .onFailure {
                    backupRepository.disable()
                    enabled = false
                    message = it.message ?: "백업을 켜지 못했어요. 잠시 후 다시 시도해 주세요."
                }
            syncing = false
        }
    }

    fun backupNow() {
        if (syncing) return
        syncing = true
        message = null
        viewModelScope.launch {
            backupRepository.backup()
                .onSuccess {
                    lastBackupAt = backupRepository.lastBackupAt
                    hasCloudBackup = true
                    message = "방금 백업했어요"
                }
                .onFailure { message = it.message ?: "백업에 실패했어요. 잠시 후 다시 시도해 주세요." }
            syncing = false
        }
    }

    /** 클라우드 → 로컬 덮어쓰기 — 반드시 확인 다이얼로그 뒤에만 호출. */
    fun restoreFromCloud() {
        if (syncing) return
        syncing = true
        message = null
        viewModelScope.launch {
            backupRepository.restore()
                .onSuccess { message = "클라우드 기록을 불러왔어요" }
                .onFailure { message = it.message ?: "불러오기에 실패했어요. 잠시 후 다시 시도해 주세요." }
            syncing = false
        }
    }
}
