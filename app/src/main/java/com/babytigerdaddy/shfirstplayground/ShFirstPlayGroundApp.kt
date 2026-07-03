package com.babytigerdaddy.shfirstplayground

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

@HiltAndroidApp
class ShFirstPlayGroundApp : Application() {
    // 크래시 로거를 attachBaseContext에서 설치 — ContentProvider(파이어베이스 자동초기화
    // FirebaseInitProvider)는 Application.onCreate보다 먼저 도는데, attachBaseContext는
    // 그보다도 먼저라 파이어베이스 초기화 단계 크래시까지 스택을 남긴다.
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        installCrashLogger()
    }

    override fun onCreate() {
        super.onCreate()
        // 애드몹 SDK 초기화. 백그라운드에서 한 번만 돌면 이후 배너/전면 광고 로드가 됨.
        MobileAds.initialize(this)
    }

    private fun installCrashLogger() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val text = "=== 매기 크래시 ===\n${java.util.Date()}\n\n$sw"
                // 파일로 (앱 전용 외부저장 — 권한 불필요)
                runCatching { File(getExternalFilesDir(null), "maegi-crash.txt").writeText(text) }
                // 다음 실행 때 화면에 띄우려고 저장
                runCatching {
                    getSharedPreferences("crash", Context.MODE_PRIVATE)
                        .edit().putString("last", text).apply()
                }
            } catch (_: Throwable) {
                // 로깅 실패는 무시 — 원래 크래시 흐름 유지
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
