package com.babytigerdaddy.shfirstplayground.ui.ads

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 앱 진입 시 하루 한 번 뜨는 광고 다이얼로그.
 *
 * - 광고 영역은 형(소보고)이 만든 [AdmobBanner] 320x50 배너를 그대로 꽂는다.
 * - 하루 한 번만: 마지막으로 띄운 날짜(yyyyMMdd)를 SharedPreferences에 저장하고,
 *   오늘 이미 띄웠으면 스킵. 앱을 껐다 켜도 오늘 안에는 다시 안 뜬다.
 * - 닫기는 우상단 X 하나. "오늘 그만 보기" 같은 옵션은 하루 한 번이라 중복이라 뺐다.
 *
 * MainActivity에서 AppNavigation() 옆에 [EntryAdGate] 한 줄만 부르면 된다.
 */
private const val PREF_NAME = "entry_ad"
private const val KEY_LAST_SHOWN = "last_shown_ymd"

private fun today(): String =
    SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(Date())

private fun shouldShowToday(context: Context): Boolean {
    val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return pref.getString(KEY_LAST_SHOWN, null) != today()
}

private fun markShownToday(context: Context) {
    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        .edit().putString(KEY_LAST_SHOWN, today()).apply()
}

/**
 * 하루 한 번 게이트. 오늘 아직 안 닫았으면 다이얼로그를 띄운다. 기록은 X로 '닫을 때'만 —
 * 그래야 닫기 전까진 계속 떠 있고, 실제로 닫아야 오늘 카운트가 된다(뜨자마자 기록하면
 * 앱을 죽여도 본 걸로 쳐서, 못 본 채로 그날 내내 안 뜨는 문제가 생김).
 */
@Composable
fun EntryAdGate() {
    val context = LocalContext.current
    var show by remember { mutableStateOf(shouldShowToday(context)) }

    if (show) {
        EntryAdDialog(onClose = {
            markShownToday(context)
            show = false
        })
    }
}

@Composable
private fun EntryAdDialog(onClose: () -> Unit) {
    // usePlatformDefaultWidth=false — 기본 다이얼로그 폭 제약을 풀어야 300dp 광고가
    // 작은 화면에서도 안 잘린다. Surface는 콘텐츠(광고 300 + 좌우 여백)에 맞춰 감싼다.
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            // 광고 300 + 좌우 여백 16씩 = 332dp 폭. 세로는 내용에 맞춰 감싼다.
            Column(modifier = Modifier.width(332.dp).padding(16.dp)) {
                // 상단: 타이틀 + X 닫기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "잠깐, 이것만 보고 가요",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Text(
                            text = "✕",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 광고 자리 — 320x50 배너 그대로. 가운데 정렬만.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    AdmobBanner()
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "매기를 무료로 쓰게 해주는 광고예요. 하루 한 번만 떠요.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
