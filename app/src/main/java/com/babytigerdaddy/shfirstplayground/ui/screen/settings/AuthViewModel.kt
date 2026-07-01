package com.babytigerdaddy.shfirstplayground.ui.screen.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.data.remote.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 화면 상태 — 로그인은 '기록을 지키고 싶은 사람이 켜는 옵션'.
 * 로그인 안 해도 앱은 그대로 로컬에서 동작. 켜면 계정에 백업 연결.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    var user by mutableStateOf<FirebaseUser?>(authRepository.currentUser)
        private set
    var loading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf<String?>(null)
        private set

    /** 구글 로그인 — 계정 선택 UI에 Activity context 필요. */
    fun signInGoogle(activityContext: Context) {
        if (loading) return
        loading = true
        errorMsg = null
        viewModelScope.launch {
            authRepository.signInWithGoogle(activityContext)
                .onSuccess { user = it }
                .onFailure { errorMsg = it.message ?: "로그인에 실패했어요. 잠시 후 다시 시도해 주세요." }
            loading = false
        }
    }

    fun signOut() {
        authRepository.signOut()
        user = null
    }

    fun clearError() { errorMsg = null }
}
