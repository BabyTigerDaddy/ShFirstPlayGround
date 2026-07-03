package com.babytigerdaddy.shfirstplayground.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 앱 진입(로딩 끝나고 보유 탭) 시 뜨는 광고 다이얼로그.
 *
 * - 광고 영역은 형(소보고)이 만든 [AdmobBanner] 300x250 배너를 그대로 꽂는다.
 * - 앱 켤 때마다 매번 뜬다(아빠 요청). '하루 한 번' 제한 없음. X로 닫으면 그 실행 동안만
 *   닫히고, 앱을 다시 켜면 또 뜬다.
 * - 배경 투명 + 타이틀/설명 글귀 없이 광고만. 위치는 화면 하단. 닫기 X는 광고 바로 위
 *   우측 모서리에 작게(투명 위라 가독성 위해 반투명 원). 결과적으로 하단에 광고 + 작은 X만.
 *
 * AppNavigation의 메인 진입 블록에서 [EntryAdGate] 한 줄만 부르면 된다.
 */
@Composable
fun EntryAdGate() {
    // rememberSaveable — 화면 회전·다크모드 변경(configuration change)으로 Activity가 재생성돼도
    // '닫음' 상태(false)가 유지된다. 그래서 한 번 X로 닫으면 회전하든 테마 바꾸든 다시 안 뜨고,
    // 프로세스가 완전히 죽고 앱을 새로 켤 때만 true로 초기화돼 다시 뜬다(아빠 요구 그대로).
    var show by rememberSaveable { mutableStateOf(true) }

    if (show) {
        EntryAdDialog(onClose = { show = false })
    }
}

@Composable
private fun EntryAdDialog(onClose: () -> Unit) {
    // usePlatformDefaultWidth=false — 폭 제약을 풀어야 화면 전체를 잡고 하단 배치가 되고
    // 300dp 광고도 안 잘린다. Surface(카드) 없이 투명 — 광고랑 X만 보인다.
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(horizontalAlignment = Alignment.End) {
                // 닫기 — 광고 바로 위 우측 모서리에 작게. 투명 배경이라 가독성 위해 반투명 원.
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                ) {
                    Text(text = "✕", color = Color.White, fontSize = 16.sp)
                }
                AdmobBanner()
            }
        }
    }
}
