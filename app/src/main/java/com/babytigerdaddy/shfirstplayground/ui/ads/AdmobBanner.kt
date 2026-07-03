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
 * 애드몹 중간 직사각형 배너(300x250). 진입 다이얼로그 안에 큼직하게(키즈노트 느낌) 넣으려고
 * 얇은 띠(320x50) 대신 중간네모로 간다. 다이얼로그 등 아무 데나 이 Composable만 부르면 광고가 뜬다.
 * 컨테이너 id 같은 거 필요 없음 — 이 자체가 광고 영역이야(문서가 다이얼로그 안에 그냥 호출).
 *
 * 지금 adUnitId는 구글 공식 배너 테스트 ID. 아빠 애드몹 계정의 실제 광고 단위 ID 나오면
 * 이 기본값만 바꾸면 진짜 수익 광고로 전환된다(다른 코드 손댈 필요 없음).
 * 광고 단위 종류는 그냥 '배너'면 되고, 크기(300x250)는 여기 코드에서 정한다.
 */
private const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun AdmobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = TEST_BANNER_UNIT_ID,
) {
    AndroidView(
        // 중간 직사각형 크기 고정(300x250 dp) — 다이얼로그 레이아웃 안 틀어지게.
        modifier = modifier
            .width(300.dp)
            .height(250.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.MEDIUM_RECTANGLE)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
