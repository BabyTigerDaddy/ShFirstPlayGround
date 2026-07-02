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
 * 클라우드 백업 화면 상태 — 자동 판정으로 덮어쓰지 않는다.
 *
 * 스위치 켜면 상황을 보고 분기:
 * · 클라우드 있음 + 로컬 비었음(새 폰/재설치) → 자동 복원
 * · 클라우드 있음 + 로컬에도 기록 있음 → 사용자에게 어느 쪽으로 갈지 물음(conflict)
 * · 클라우드 없음 → 첫 백업
 * 되돌릴 수 없는 덮어쓰기는 어느 방향이든 반드시 사용자 선택을 거친다.
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

    /** 켜기 시점에 클라우드·로컬 양쪽 다 기록이 있어 사용자 선택이 필요한 상태. */
    var conflict by mutableStateOf(false)
        private set

    /** 로그인 상태 바뀔 때 클라우드에 백업 있는지 갱신 — '불러오기' 버튼 노출 판단용. */
    fun refreshCloudState() {
        viewModelScope.launch { hasCloudBackup = backupRepository.hasCloudBackup() }
    }

    /** 백업 스위치 — 켜면 상황 보고 분기(복원/물음/첫 백업). 끄면 플래그만 내림. */
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
            val cloud = backupRepository.hasCloudBackup()
            val localEmpty = backupRepository.isLocalEmpty()
            when {
                // 양쪽 다 기록 있음 — 자동으로 안 덮고 사용자가 고른다. 스위치는 아직 안 켬.
                cloud && !localEmpty -> {
                    hasCloudBackup = true
                    conflict = true
                }
                // 새 폰/재설치 — 클라우드에서 자동 복원
                cloud && localEmpty -> runRestore(auto = true)
                // 클라우드 없음 — 이 폰 기록으로 첫 백업
                else -> runFirstBackup()
            }
            syncing = false
        }
    }

    /** 양쪽 다 있을 때 사용자가 '클라우드 걸로'를 고름 — 폰 기록을 클라우드 백업으로 덮어씀. */
    fun resolveWithCloud() {
        conflict = false
        if (syncing) return
        syncing = true
        viewModelScope.launch {
            runRestore(auto = false)
            syncing = false
        }
    }

    /** 양쪽 다 있을 때 사용자가 '지금 폰 걸로'를 고름 — 클라우드를 폰 기록으로 덮어씀. */
    fun resolveWithLocal() {
        conflict = false
        if (syncing) return
        syncing = true
        viewModelScope.launch {
            runFirstBackup()
            syncing = false
        }
    }

    /** 선택 안 하고 닫음 — 스위치 안 켜지고 아무것도 안 덮는다. */
    fun dismissConflict() {
        conflict = false
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

    private suspend fun runRestore(auto: Boolean) {
        backupRepository.enable()
        backupRepository.restore()
            .onSuccess {
                enabled = true
                hasCloudBackup = true
                message = if (auto) "클라우드 기록을 불러왔어요 (새 폰 복원)" else "클라우드 기록을 불러왔어요"
            }
            .onFailure {
                backupRepository.disable()
                enabled = false
                message = it.message ?: "불러오기에 실패했어요. 잠시 후 다시 시도해 주세요."
            }
    }

    private suspend fun runFirstBackup() {
        backupRepository.enable()
        backupRepository.backup()
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
    }
}
