package com.babytigerdaddy.shfirstplayground.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.babytigerdaddy.shfirstplayground.domain.model.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * 단발성 현재 위치 조회 헬퍼.
 *
 * - FusedLocationProviderClient.getCurrentLocation() 사용 — last-known 보다 정확.
 * - 권한 부재·위치 서비스 꺼짐 시 null 반환. caller가 fallback 처리.
 * - 5~7세 아동 위치 데이터 처리 원칙: 호출 시점은 사용자 명시적 trigger 후 한 번씩만,
 *   결과는 기기 내부 Room/Preferences에만 저장 (서버 전송 코드 X).
 */
@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    /** 권한 있고 GPS 활성이면 현재 위치, 아니면 null. */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasPermission()) return null
        return suspendCancellableCoroutine { cont ->
            val token = com.google.android.gms.tasks.CancellationTokenSource()
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, token.token)
                .addOnSuccessListener { android ->
                    cont.resume(
                        android?.let {
                            Location(latitude = it.latitude, longitude = it.longitude, label = null)
                        },
                    )
                }
                .addOnFailureListener { cont.resume(null) }
            cont.invokeOnCancellation { token.cancel() }
        }
    }

    fun hasPermission(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        val coarse = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        return fine == PackageManager.PERMISSION_GRANTED ||
            coarse == PackageManager.PERMISSION_GRANTED
    }
}
