package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.DailyPnlPoint
import com.babytigerdaddy.shfirstplayground.domain.model.PnlSummary
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.Line

/** 추이 화면 — 누적 손익선 + 요약 카드. */
@Composable
fun TrendScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(
                text = "단타 추이",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatWon(summary.totalRealized),
                style = MaterialTheme.typography.headlineLarge,
                color = pnlColor(summary.totalRealized),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "누적 실현손익 · 기록 ${summary.recordedDays}일",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        CumulativeChart(summary.daily, summary.totalRealized)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "승률",
                value = "${(summary.winRate * 100).toInt()}%",
                sub = "이익 ${summary.winDays} · 손실 ${summary.lossDays}",
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

        summary.bestDay?.let { HighlightCard(title = "이번 흐름 베스트 날", point = it) }
        summary.worstDay?.let { HighlightCard(title = "가장 아팠던 날", point = it) }

        if (summary.recordedDays == 0) {
            Text(
                text = "아직 기록이 없어요. 입력 탭에서 오늘 손익을 적어보세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CumulativeChart(daily: List<DailyPnlPoint>, total: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(240.dp).padding(16.dp), contentAlignment = Alignment.Center) {
            if (daily.size < 2) {
                Text(
                    text = "이틀 이상 기록되면\n누적 곡선이 그려져요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                val lineColor = pnlColor(total)
                LineChart(
                    modifier = Modifier.fillMaxSize(),
                    data = listOf(
                        Line(
                            label = "누적 손익",
                            values = daily.map { it.cumulative.toDouble() },
                            color = SolidColor(lineColor),
                            firstGradientFillColor = lineColor.copy(alpha = 0.4f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = true,
                        ),
                    ),
                    animationMode = AnimationMode.Together { it * 120L },
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.onSurface,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = accent)
            Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HighlightCard(title: String, point: DailyPnlPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = pnlColor(point.realizedPnl).copy(alpha = 0.10f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "${point.date}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatWon(point.realizedPnl),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = pnlColor(point.realizedPnl),
                )
            }
            if (point.note.isNotBlank()) {
                Text(text = "\"${point.note}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
