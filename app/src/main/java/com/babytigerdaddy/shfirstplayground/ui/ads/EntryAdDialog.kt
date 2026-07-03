package com.babytigerdaddy.shfirstplayground.ui.ads

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 앱 진입(로딩 끝나고 보유 탭) 시 뜨는 광고 다이얼로그.
 *
 * - 광고 영역은 형(소보고)이 만든 [AdmobBanner] 300x250 배너를 그대로 꽂는다.
 * - 앱 켤 때마다 매번 뜬다(아빠 요청). '하루 한 번' 제한은 뺐다. X로 닫으면 그 실행 동안만
 *   닫히고, 앱을 다시 켜면 또 뜬다. (출시 때 하루 한 번으로 되돌리려면 show 초기값을
 *   SharedPreferences 날짜 게이트로 다시 감싸면 된다.)
 * - 닫기는 우상단 X 하나.
 *
 * AppNavigation의 메인 진입 블록에서 [EntryAdGate] 한 줄만 부르면 된다.
 */
@Composable
fun EntryAdGate() {
    var show by remember { mutableStateOf(true) }

    if (show) {
        EntryAdDialog(onClose = { show = false })
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
