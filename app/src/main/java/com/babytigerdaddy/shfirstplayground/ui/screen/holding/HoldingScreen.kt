package com.babytigerdaddy.shfirstplayground.ui.screen.holding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.FlatGray
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.LossBlue
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.ProfitRed
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.TradeCard
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.TradeInk
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.TradeMuted
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.formatWon
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.label
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.pnlColor
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.tint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 보유 종목 배지 — 이미지 톤 (진입 보라 / 보유 N일 초록)
private val EntryBadgeBg = Color(0xFFECEAFB)
private val EntryBadgeFg = Color(0xFF6B5BD2)
private val DaysBadgeBg = Color(0xFFDDF3E4)
private val DaysBadgeFg = Color(0xFF1B9E54)

/** 보유 종목 탭 — 아직 들고 있는 종목의 평가손익 추적 (와이프용, 세련 톤). */
@Composable
fun HoldingScreen(viewModel: HoldingViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    var sellTarget by remember { mutableStateOf<Holding?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { SummaryHeader(summary.totalEval, summary.totalReturnRate, summary.totalPnl, summary.totalCost) }
        item {
            FilledTonalButton(
                onClick = { showAdd = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) { Text(text = "+  종목 추가", fontSize = 15.sp) }
        }
        if (summary.holdings.isEmpty()) {
            item {
                Text(
                    text = "들고 있는 종목을 추가하면\n평가손익과 보유일이 자동으로 추적돼요.",
                    fontSize = 14.sp, color = TradeMuted,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            items(summary.holdings, key = { it.id }) { h ->
                HoldingCard(
                    holding = h,
                    onSell = { sellTarget = h },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }

    if (showAdd) AddHoldingDialog(viewModel = viewModel, onClose = { showAdd = false })
    sellTarget?.let { h ->
        SellDialog(holding = h, onConfirm = { mood, note -> viewModel.sell(h, mood, note); sellTarget = null }, onDismiss = { sellTarget = null })
    }
}

@Composable
private fun SummaryHeader(totalEval: Long, rate: Double, totalPnl: Long, totalCost: Long) {
    val tone = pnlColor(totalPnl)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "총 평가금액", fontSize = 13.sp, color = TradeMuted)
            Text(text = formatWon(totalEval).removePrefix("+"), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TradeInk)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 4.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(tone.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(text = "${if (rate >= 0) "+" else ""}${"%.2f".format(rate * 100)}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = tone)
                }
                Text(text = "평가손익 ${formatWon(totalPnl)}", fontSize = 13.sp, color = tone)
            }
            Text(text = "총 매수금액 ${formatWon(totalCost).removePrefix("+")}", fontSize = 12.sp, color = TradeMuted, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun HoldingCard(holding: Holding, onSell: () -> Unit, modifier: Modifier = Modifier) {
    val tone = pnlColor(holding.evalPnl)
    val days = holding.holdingDays(LocalDate.now())
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TradeCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = holding.ticker, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TradeInk)
                    HoldingBadge(days)
                }
                Text(
                    text = "${if (holding.returnRate >= 0) "+" else ""}${"%.2f".format(holding.returnRate * 100)}%",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = tone,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${comma(holding.buyPrice)} → ${comma(holding.currentPrice)}" + if (holding.quantity > 1) "  ·  ${holding.quantity}주" else "",
                        fontSize = 12.5.sp, color = TradeMuted,
                    )
                    Text(text = "평가손익 ${formatWon(holding.evalPnl)}", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = tone)
                    Text(text = "편입 ${holding.entryDate.format(DateTimeFormatter.ofPattern("MM/dd"))}", fontSize = 11.sp, color = TradeMuted)
                }
                OutlinedButton(onClick = onSell, shape = RoundedCornerShape(12.dp)) {
                    Text(text = "매도", fontSize = 13.sp, color = LossBlue)
                }
            }
        }
    }
}

@Composable
private fun HoldingBadge(days: Long) {
    val (bg, fg, label) = if (days <= 0L) Triple(EntryBadgeBg, EntryBadgeFg, "진입") else Triple(DaysBadgeBg, DaysBadgeFg, "${days}일")
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 9.dp, vertical = 3.dp)) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHoldingDialog(viewModel: HoldingViewModel, onClose: () -> Unit) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    var showDate by remember { mutableStateOf(false) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onClose,
        title = { Text(text = "보유 종목 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = input.ticker, onValueChange = viewModel::onTickerChange, singleLine = true, label = { Text("종목명") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = input.buyPriceText, onValueChange = viewModel::onBuyPriceChange, singleLine = true, label = { Text("매수가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = input.currentPriceText, onValueChange = viewModel::onCurrentPriceChange, singleLine = true, label = { Text("현재가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = input.quantityText, onValueChange = viewModel::onQuantityChange, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text(text = "편입일 · ${input.entryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}", color = TradeInk)
                }
            }
        },
        confirmButton = { TextButton(onClick = { viewModel.addHolding(); onClose() }, enabled = input.canSave) { Text("추가") } },
        dismissButton = { TextButton(onClick = onClose) { Text("취소") } },
    )
    if (showDate) {
        val state = rememberDatePickerState(initialSelectedDateMillis = input.entryDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { ms ->
                        viewModel.onEntryDateChange(Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    showDate = false
                }) { Text("확인") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("취소") } },
        ) { DatePicker(state = state) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SellDialog(holding: Holding, onConfirm: (TradeMood, String) -> Unit, onDismiss: () -> Unit) {
    var mood by remember { mutableStateOf(TradeMood.FLAT) }
    var note by remember { mutableStateOf("") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "${holding.ticker} 매도") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "실현손익 ${formatWon(holding.evalPnl)} 이 오늘 매매일지로 넘어가요.", fontSize = 13.sp, color = pnlColor(holding.evalPnl))
                Text(text = "오늘 마음", fontSize = 12.sp, color = TradeMuted)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TradeMood.entries.forEach { m ->
                        FilterChip(selected = m == mood, onClick = { mood = m }, label = { Text(m.label(), fontSize = 12.sp) },
                            leadingIcon = { Box(Modifier.size(9.dp).clip(CircleShape).background(m.tint())) })
                    }
                }
                OutlinedTextField(value = note, onValueChange = { note = it }, singleLine = true, label = { Text("한 줄 메모 (선택)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(mood, note.trim()) }) { Text("매도 → 일지로") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } },
    )
}

private fun comma(v: Long): String = v.toString().reversed().chunked(3).joinToString(",").reversed()
