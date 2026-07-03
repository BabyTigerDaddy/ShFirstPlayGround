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
 * 애드몹 배너. 이 Composable만 부르면 그 자리에 광고가 뜬다(컨테이너 id 필요 없음).
 *
 * 두 가지 크기:
 * - [AdmobBannerSize.MEDIUM_RECTANGLE] 300x250 — 진입 팝업 안에 큼직하게(키즈노트 느낌). 기본값.
 * - [AdmobBannerSize.BANNER] 320x50 — 각 탭 하단에 거슬리지 않게 얇은 띠 한 줄.
 *
 * adUnitId는 아빠 실제 애드몹 계정의 '진입팝업배너' 광고 단위. 팝업·페이지 배너 둘 다 지금은
 * 이 하나로 돌린다(한 광고 단위로 여러 크기 로드 가능). 나중에 팝업/페이지 수익을 따로 보고
 * 싶으면 광고 단위를 하나 더 만들어 페이지용에 넘기면 된다.
 */
private const val BANNER_UNIT_ID = "ca-app-pub-7471786568746580/8868944530"

/** 배너 크기 — AdSize와 dp 크기를 함께 묶는다(레이아웃이 광고 크기에 맞게 고정되게). */
enum class AdmobBannerSize(val adSize: AdSize, val widthDp: Int, val heightDp: Int) {
    MEDIUM_RECTANGLE(AdSize.MEDIUM_RECTANGLE, 300, 250), // 팝업용 중간 네모
    BANNER(AdSize.BANNER, 320, 50),                      // 페이지 하단용 얇은 띠
}

@Composable
fun AdmobBanner(
    modifier: Modifier = Modifier,
    size: AdmobBannerSize = AdmobBannerSize.MEDIUM_RECTANGLE,
    adUnitId: String = BANNER_UNIT_ID,
) {
    AndroidView(
        // 크기 고정 — 레이아웃(팝업·탭 하단)이 광고 크기에 안 흔들리게.
        modifier = modifier
            .width(size.widthDp.dp)
            .height(size.heightDp.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(size.adSize)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
