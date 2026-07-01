package com.babytigerdaddy.shfirstplayground.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.babytigerdaddy.shfirstplayground.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 계정 인증 — 로그인은 '기록을 지키고 싶은 사람이 켜는 옵션'(입장 장벽 아님).
 *
 * 구글: Credential Manager로 계정 선택 UI를 띄우고, 받은 ID 토큰으로 파이어베이스 인증.
 * (카카오는 커스텀 토큰 방식이라 앱키 확보 후 별도 연결.)
 */
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val appContext: Context,
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    /**
     * 구글 계정으로 로그인.
     * @param activityContext 자격증명 UI를 띄우기 위한 Activity context.
     */
    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> = try {
        val credentialManager = CredentialManager.create(activityContext)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(appContext.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val response = credentialManager.getCredential(activityContext, request)
        val googleCredential = GoogleIdTokenCredential.createFrom(response.credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
        val authResult = auth.signInWithCredential(firebaseCredential).await()
        val user = authResult.user ?: error("로그인 결과에 사용자 정보가 없음")
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun signOut() = auth.signOut()
}
