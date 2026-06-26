package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.TickerStat
import com.babytigerdaddy.shfirstplayground.domain.model.WeekdayStat
import java.time.DayOfWeek

/** 통계 탭 — 한 줄 인사이트 + 요일별 승률 + 종목 순위 + 핵심 카드. 매매 습관을 데이터로. */
@Composable
fun StatsScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val hasData = stats.weekday.any { it.tradedDays > 0 }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(text = "매매 습관", fontSize = 13.sp, color = TradeMuted)

        // 한 줄 인사이트
        InsightCard(insight(stats.weekday, hasData))

        // 핵심 카드
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MiniCard("하루 평균", formatWon(stats.avgDailyPnl), pnlColor(stats.avgDailyPnl), Modifier.weight(1f))
            MiniCard("최대 연속손실", "${stats.maxLossStreak}일", if (stats.maxLossStreak > 0) LossBlue else TradeInk, Modifier.weight(1f))
        }
        MiniCard("최대 낙폭 (고점 대비)", formatWon(-stats.maxDrawdown), if (stats.maxDrawdown > 0) LossBlue else TradeInk, Modifier.fillMaxWidth())

        // 요일별 승률
        SectionCard("요일별 승률") {
            if (!hasData) {
                Text(text = "매매 기록이 쌓이면 요일 습관이 보여요", fontSize = 13.sp, color = TradeMuted)
            } else {
                stats.weekday.forEach { WeekdayRow(it) }
            }
        }

        // 종목 순위
        SectionCard("종목별 손익 순위") {
            if (stats.tickers.isEmpty()) {
                Text(text = "입력 화면에서 종목을 같이 적으면 순위가 만들어져요", fontSize = 13.sp, color = TradeMuted)
            } else {
                stats.tickers.forEachIndexed { i, t -> TickerRow(i + 1, t) }
                Text(
                    text = "* 하루 합산 손익 기준 연관 손익 — 종목별 정밀 손익은 추후 고도화",
                    fontSize = 10.sp, color = TradeMuted, modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

private fun insight(weekday: List<WeekdayStat>, hasData: Boolean): String {
    if (!hasData) return "기록이 더 쌓이면 당신의 매매 습관을 짚어줄게요."
    val traded = weekday.filter { it.tradedDays >= 2 }.ifEmpty { weekday.filter { it.tradedDays > 0 } }
    val worst = traded.minByOrNull { it.winRate } ?: return "꾸준히 기록 중 — 아직 뚜렷한 약점 요일은 없어요."
    val best = traded.maxByOrNull { it.winRate }
    val wd = worst.dayOfWeek.kr()
    return if (best != null && best.dayOfWeek != worst.dayOfWeek && best.winRate - worst.winRate > 0.15) {
        "${wd}요일 승률 ${(worst.winRate * 100).toInt()}% — ${best.dayOfWeek.kr()}요일(${(best.winRate * 100).toInt()}%)보다 약해요. ${wd}요일을 조심."
    } else {
        "${wd}요일 승률 ${(worst.winRate * 100).toInt()}% — ${wd}요일을 조심."
    }
}

@Composable
private fun InsightCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TradeInk),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "💡", fontSize = 18.sp)
            Text(
                text = text,
                fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
    }
}

@Composable
private fun MiniCard(title: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCard),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, fontSize = 12.sp, color = TradeMuted)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCard),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TradeInk)
            content()
        }
    }
}

@Composable
private fun WeekdayRow(stat: WeekdayStat) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = stat.dayOfWeek.kr(), fontSize = 13.sp, color = TradeInk, modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier.weight(1f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(TradeLine),
        ) {
            if (stat.tradedDays > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(stat.winRate.toFloat().coerceIn(0.02f, 1f))
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(ProfitRed),
                )
            }
        }
        Text(
            text = if (stat.tradedDays > 0) "${(stat.winRate * 100).toInt()}%" else "-",
            fontSize = 12.sp, color = TradeMuted, modifier = Modifier.width(36.dp),
        )
    }
}

@Composable
private fun TickerRow(rank: Int, t: TickerStat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "$rank", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TradeMuted, modifier = Modifier.width(18.dp))
            Column {
                Text(text = t.ticker, fontSize = 14.sp, color = TradeInk)
                Text(text = "${t.tradedDays}일 매매", fontSize = 11.sp, color = TradeMuted)
            }
        }
        Text(
            text = formatWon(t.totalPnl),
            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pnlColor(t.totalPnl),
        )
    }
}

private fun DayOfWeek.kr(): String = when (this) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}
