package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.MoodStat
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import java.time.format.DateTimeFormatter

/** 회고 탭 — 기분이 손익에 미친 영향 + 메모 타임라인. "과매매 찍은 날 진짜 더 잃었나"를 숫자로. */
@Composable
fun RetroScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val entries by viewModel.entries.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(text = "회고", fontSize = 13.sp, color = TradeMuted)

        // 기분별 손익 영향 (데이터 레이어 집계 — stats.mood)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TradeCard),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "기분별 평균 손익", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TradeInk)
                moodInsight(stats.mood)?.let {
                    Text(text = it, fontSize = 13.sp, color = TradeInk)
                }
                if (stats.mood.isEmpty()) {
                    Text(text = "매매를 기록하면 기분별 패턴이 보여요", fontSize = 13.sp, color = TradeMuted)
                } else {
                    stats.mood.forEach { MoodRow(it) }
                }
            }
        }

        // 메모 타임라인 (entries — 메모 남긴 날만, 최신순)
        Text(text = "메모 타임라인", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TradeInk)
        val noted = entries.filter { it.note.isNotBlank() }.sortedByDescending { it.date }
        if (noted.isEmpty()) {
            Text(text = "입력할 때 한 줄 메모를 남기면 여기 타임라인으로 쌓여요", fontSize = 13.sp, color = TradeMuted)
        } else {
            noted.forEach { TimelineItem(it) }
        }
    }
}

/** stats.mood는 평균 손해 큰 기분이 맨 위 — 첫 항목이 음수면 그게 약점. */
private fun moodInsight(mood: List<MoodStat>): String? {
    val worst = mood.firstOrNull() ?: return null
    return if (worst.avgPnl < 0 && worst.days >= 2) {
        "'${worst.mood.label()}' 찍은 날 평균 ${formatWon(worst.avgPnl)} — 그런 날을 줄이는 게 답."
    } else {
        null
    }
}

@Composable
private fun MoodRow(stat: MoodStat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(stat.mood.tint()))
            Text(text = stat.mood.label(), fontSize = 14.sp, color = TradeInk)
            Text(text = "${stat.days}일", fontSize = 11.sp, color = TradeMuted)
        }
        Text(
            text = formatWon(stat.avgPnl),
            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pnlColor(stat.avgPnl),
        )
    }
}

@Composable
private fun TimelineItem(entry: TradeJournalEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCard),
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(entry.mood.tint()))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = entry.date.format(DateTimeFormatter.ofPattern("M.d (E)")),
                        fontSize = 12.sp, color = TradeMuted,
                    )
                    Text(
                        text = formatWon(entry.realizedPnl),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = pnlColor(entry.realizedPnl),
                    )
                }
                Text(text = entry.note, fontSize = 14.sp, color = TradeInk)
                if (entry.tickers.isNotEmpty()) {
                    Text(text = entry.tickers.joinToString(" · "), fontSize = 11.sp, color = TradeMuted)
                }
            }
        }
    }
}
