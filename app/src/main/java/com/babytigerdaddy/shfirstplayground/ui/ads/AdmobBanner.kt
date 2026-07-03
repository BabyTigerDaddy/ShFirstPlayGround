package com.babytigerdaddy.shfirstplayground.ui.ads

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * 애드몹 표준 배너(320x50). 다이얼로그 등 아무 데나 이 Composable만 부르면 광고가 뜬다.
 * 컨테이너 id 같은 거 필요 없음 — 이 자체가 광고 영역이야(문서가 다이얼로그 안에 그냥 호출).
 *
 * 지금 adUnitId는 구글 공식 배너 테스트 ID. 아빠 애드몹 계정의 실제 광고 단위 ID 나오면
 * 이 기본값만 바꾸면 진짜 수익 광고로 전환된다(다른 코드 손댈 필요 없음).
 */
private const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun AdmobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = TEST_BANNER_UNIT_ID,
) {
    AndroidView(
        // 표준 배너 크기 고정(320x50 dp) — 다이얼로그 레이아웃 안 틀어지게.
        modifier = modifier
            .width(320.dp)
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
