package com.babytigerdaddy.shfirstplayground

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babytigerdaddy.shfirstplayground.ui.ads.EntryAdGate
import com.babytigerdaddy.shfirstplayground.ui.navigation.AppNavigation
import com.babytigerdaddy.shfirstplayground.ui.theme.ShFirstPlayGroundTheme
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeController
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemePalette
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var themeController: ThemeController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 직전 실행에서 크래시가 있었으면 스택을 화면에 띄운다(파일 접근이 어려울 때 대비).
        val crashPref = getSharedPreferences("crash", Context.MODE_PRIVATE)
        val lastCrash = crashPref.getString("last", null)
        setContent {
            if (lastCrash != null) {
                CrashReportScreen(lastCrash) {
                    crashPref.edit().clear().apply()
                    recreate()
                }
            } else {
                // 모양은 사용자 선택 유지, 화이트/다크는 시스템 설정 따라 자동 전환.
                val settings by themeController.settings.collectAsState()
                val effective = settings.copy(
                    palette = if (isSystemInDarkTheme()) ThemePalette.DARK else ThemePalette.LIGHT,
                )
                ShFirstPlayGroundTheme(settings = effective) {
                    AppNavigation()
                    // 진입 시 하루 한 번 광고 다이얼로그(Dialog라 화면 위에 뜸).
                    EntryAdGate()
                }
            }
        }
    }
}

@Composable
private fun CrashReportScreen(log: String, onClear: () -> Unit) {
    Surface(color = Color(0xFF161A20)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        ) {
            Text("앱이 튕겼어요", color = Color(0xFFFF6B6B), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "아래 에러 화면을 통째로 캡처해서 보내주세요. 이게 원인 잡는 열쇠예요.",
                color = Color(0xFFB8C0CC), fontSize = 13.sp,
            )
            Spacer(Modifier.height(14.dp))
            Button(onClick = onClear) { Text("지우고 다시 실행") }
            Spacer(Modifier.height(14.dp))
            Text(log, color = Color(0xFFDDE3EA), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
    }
}
