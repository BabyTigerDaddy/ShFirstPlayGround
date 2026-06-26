package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.TradeJournalEntry
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs

/** 달력 탭 — 한 달을 잔디밭처럼. 번 날은 빨강·잃은 날은 파랑, 손익 클수록 진하게. 칸 누르면 그날 상세. */
@Composable
fun CalendarScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf<LocalDate?>(null) }

    val byDate = remember(entries) { entries.associateBy { it.date } }
    val monthEntries = remember(entries, month) {
        entries.filter { YearMonth.from(it.date) == month && it.realizedPnl != 0L }
    }
    val maxAbs = remember(monthEntries) { monthEntries.maxOfOrNull { abs(it.realizedPnl) } ?: 1L }
    val monthTotal = remember(entries, month) {
        entries.filter { YearMonth.from(it.date) == month }.sumOf { it.realizedPnl }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 월 이동 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavArrow("‹") { month = month.minusMonths(1); selected = null }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${month.year}.${"%02d".format(month.monthValue)}",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TradeInk,
                )
                Text(text = "이번 달 ${formatWon(monthTotal)}", fontSize = 12.sp, color = pnlColor(monthTotal))
            }
            NavArrow("›") { month = month.plusMonths(1); selected = null }
        }

        // 요일 헤더 (월~일)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("월", "화", "수", "목", "금", "토", "일").forEach {
                Text(
                    text = it, fontSize = 11.sp, color = TradeMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // 그리드
        val firstOffset = month.atDay(1).dayOfWeek.value - 1 // 월=0
        val length = month.lengthOfMonth()
        val cells = buildList {
            repeat(firstOffset) { add(null) }
            for (d in 1..length) add(month.atDay(d))
        }
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 0 until 7) {
                    val date = week.getOrNull(i)
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                        if (date != null) {
                            DayCell(
                                day = date.dayOfMonth,
                                entry = byDate[date],
                                maxAbs = maxAbs,
                                isSelected = date == selected,
                                isToday = date == LocalDate.now(),
                                onClick = { selected = if (selected == date) null else date },
                            )
                        }
                    }
                }
            }
        }

        // 범례
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            LegendDot(ProfitRed, "익절")
            LegendDot(LossBlue, "손절")
            LegendDot(FlatGray.copy(alpha = 0.4f), "쉼")
        }

        // 선택 날짜 상세
        selected?.let { date ->
            DayDetail(date = date, entry = byDate[date])
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    entry: TradeJournalEntry?,
    maxAbs: Long,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val bg = when {
        entry == null -> TradeCard
        entry.realizedPnl == 0L -> FlatGray.copy(alpha = 0.22f)
        else -> {
            val base = if (entry.realizedPnl > 0) ProfitRed else LossBlue
            val intensity = (abs(entry.realizedPnl).toFloat() / maxAbs).coerceIn(0.15f, 1f)
            base.copy(alpha = 0.20f + 0.75f * intensity)
        }
    }
    val strong = entry != null && entry.realizedPnl != 0L
    val fg = if (strong) Color.White else TradeMuted
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .then(if (isSelected) Modifier.border(2.dp, TradeInk, RoundedCornerShape(8.dp)) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$day",
            fontSize = 13.sp,
            color = fg,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun DayDetail(date: LocalDate, entry: TradeJournalEntry?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry == null) TradeCard else pnlColor(entry.realizedPnl).copy(alpha = 0.10f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("M월 d일 (E)")),
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TradeInk,
            )
            if (entry == null) {
                Text(text = "기록 없음", fontSize = 13.sp, color = TradeMuted)
            } else {
                Text(
                    text = formatWon(entry.realizedPnl),
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = pnlColor(entry.realizedPnl),
                )
                Text(text = "기분 · ${entry.mood.label()}", fontSize = 12.sp, color = TradeMuted)
                if (entry.tickers.isNotEmpty()) {
                    Text(text = "종목 · ${entry.tickers.joinToString(", ")}", fontSize = 12.sp, color = TradeMuted)
                }
                if (entry.note.isNotBlank()) {
                    Text(text = "\"${entry.note}\"", fontSize = 13.sp, color = TradeInk)
                }
            }
        }
    }
}

@Composable
private fun NavArrow(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(TradeCard)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(text = symbol, fontSize = 20.sp, color = TradeInk)
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.padding(1.dp).clip(RoundedCornerShape(3.dp)).background(color).padding(6.dp))
        Text(text = label, fontSize = 11.sp, color = TradeMuted)
    }
}
