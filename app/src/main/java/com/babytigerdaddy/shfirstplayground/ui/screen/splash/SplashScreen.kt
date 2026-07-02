package com.babytigerdaddy.shfirstplayground.ui.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babytigerdaddy.shfirstplayground.R

private val BrandMint = Color(0xFF3DD6C0)
private val BrandPurple = Color(0xFF6C5CE7)

/**
 * 스플래시 — 앱 켤 때 초기화(종목 목록·시세) 도는 동안 보여주는 브랜드 화면.
 * 갈매기 로고가 살짝 떠오르며 등장, 로고가 주인공이고 로딩 표시는 조연.
 *
 * @param statusText 지금 뭘 하는지 한 줄 ("종목 목록 준비 중..." 등). 비면 로딩 줄만.
 */
@Composable
fun SplashScreen(statusText: String, modifier: Modifier = Modifier) {
    // 등장 모션 — 페이드인 + 아래에서 살짝 떠오름 (한 번만)
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(durationMillis = 900, easing = EaseOutCubic)) }
    val rise = (1f - appear.value) * 28f

    Box(
        modifier = modifier.fillMaxSize().background(
            Brush.linearGradient(
                colors = listOf(BrandMint, BrandPurple),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            ),
        ),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
                .offset(y = rise.dp)
                .alpha(appear.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "매기",
                modifier = Modifier.size(148.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text("매기", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 6.sp)
            Spacer(Modifier.height(10.dp))
            Text("매일 적는 매매기록", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.9f), letterSpacing = 2.sp)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LinearProgressIndicator(
                modifier = Modifier.width(120.dp).height(3.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f),
            )
            if (statusText.isNotBlank()) {
                Text(statusText, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}
