package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.DailyPnlPoint
import java.time.format.DateTimeFormatter

/** 추이 화면 — 누적 손익선 + 한눈 요약. 엄마가 열어도 '오늘 땄나'가 바로 보이게. */
@Composable
fun TrendScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 헤더 — 누적 큰 숫자(카운트업)
        Column {
            Text(text = "단타 추이", fontSize = 13.sp, color = TradeMuted)
            AnimatedWon(
                value = summary.totalRealized,
                color = pnlColor(summary.totalRealized),
            )
            Text(
                text = "누적 실현손익 · 기록 ${summary.recordedDays}일",
                fontSize = 12.sp,
                color = TradeMuted,
            )
        }

        // 누적선 차트
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TradeCard),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(232.dp).padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (summary.daily.size < 2) {
                    Text(
                        text = "이틀 이상 기록되면\n누적 곡선이 그려져요",
                        fontSize = 14.sp,
                        color = TradeMuted,
                    )
                } else {
                    CumulativeLineChart(summary.daily, Modifier.fillMaxSize())
                }
            }
        }

        // 승률 / 요즘 흐름
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "승률",
                value = "${(summary.winRate * 100).toInt()}%",
                sub = "이익 ${summary.winDays} · 손실 ${summary.lossDays}",
                accent = TradeInk,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                title = "요즘 흐름",
                value = streakText(summary.currentStreak),
                sub = streakSub(summary.currentStreak),
                accent = streakColor(summary.currentStreak),
                modifier = Modifier.weight(1f),
            )
        }

        // 베스트 / 최악 날 — 등장 모션
        summary.bestDay?.let { Reveal { HighlightCard("이번 흐름 베스트 날", it) } }
        summary.worstDay?.let { Reveal { HighlightCard("가장 아팠던 날", it) } }

        if (summary.recordedDays == 0) {
            Text(
                text = "아직 기록이 없어요. 입력 탭에서 오늘 손익을 적어보세요.",
                fontSize = 14.sp,
                color = TradeMuted,
            )
        }
    }
}

/** 0 → 실제 누적까지 숫자가 굴러 올라가는 카운트업. */
@Composable
private fun AnimatedWon(value: Long, color: Color) {
    val animated by animateIntAsState(
        targetValue = value.toInt(),
        animationSpec = tween(durationMillis = 900),
        label = "won-count",
    )
    Text(
        text = formatWon(animated.toLong()),
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        color = color,
    )
}

/** 첫 표시 때 살짝 커지며 떠오르는 카드 래퍼 (M3 Expressive 톤). */
@Composable
private fun Reveal(content: @Composable () -> Unit) {
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { show = true }
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(tween(360)) + scaleIn(tween(360), initialScale = 0.92f),
    ) {
        content()
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    sub: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCard),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, fontSize = 12.sp, color = TradeMuted)
            Text(text = value, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = accent)
            Text(text = sub, fontSize = 11.sp, color = TradeMuted)
        }
    }
}

@Composable
private fun HighlightCard(title: String, point: DailyPnlPoint) {
    val tone = pnlColor(point.realizedPnl)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tone.copy(alpha = 0.10f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, fontSize = 12.sp, color = TradeMuted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = point.date.format(DateTimeFormatter.ofPattern("M월 d일")),
                    fontSize = 14.sp,
                    color = TradeInk,
                )
                Text(
                    text = formatWon(point.realizedPnl),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = tone,
                )
            }
            if (point.note.isNotBlank()) {
                Text(text = "\"${point.note}\"", fontSize = 13.sp, color = TradeMuted)
            }
        }
    }
}

private fun streakText(streak: Int): String = when {
    streak > 0 -> "${streak}일 연속"
    streak < 0 -> "${-streak}일 연속"
    else -> "-"
}

private fun streakSub(streak: Int): String = when {
    streak > 0 -> "익절 행진"
    streak < 0 -> "손절 중"
    else -> "쉬는 중"
}

private fun streakColor(streak: Int): Color = when {
    streak > 0 -> ProfitRed
    streak < 0 -> LossBlue
    else -> FlatGray
}
