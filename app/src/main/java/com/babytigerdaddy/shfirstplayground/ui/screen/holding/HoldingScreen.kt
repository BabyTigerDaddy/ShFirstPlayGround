package com.babytigerdaddy.shfirstplayground.ui.screen.holding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.LossBlue
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.ProfitRed
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.formatWon
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.pnlColor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 보유노트 전용 톤 — 시인성 최우선 (글자 거의 검정, 배경/카드 대비)
private val HoldBg = Color(0xFFEDEFF3)
private val HoldCard = Color(0xFFFFFFFF)
private val HoldInk = Color(0xFF0E1216)   // 본문 — 거의 검정
private val HoldSub = Color(0xFF4B5562)   // 보조 — 진한 회색(또렷)
private val HoldFaint = Color(0xFF818B99)
private val HoldLine = Color(0xFFE2E6EC)
// 보유일 배지 — 와이프 이미지 톤
private val EntryBadgeBg = Color(0xFFEAE7FB)
private val EntryBadgeFg = Color(0xFF5A49C8)
private val DaysBadgeBg = Color(0xFFD7F0DF)
private val DaysBadgeFg = Color(0xFF15883F)

/** 보유노트 — 와이프용 보유현황 단독 앱 메인. 시인성·세련 톤. */
@Composable
fun HoldingScreen(viewModel: HoldingViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    var sellTarget by remember { mutableStateOf<Holding?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(HoldBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(text = "보유노트", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = HoldInk)
                    Text(text = "지금 들고 있는 종목", fontSize = 13.sp, color = HoldSub)
                }
            }
            item { SummaryHeader(summary.totalEval, summary.totalReturnRate, summary.totalPnl, summary.totalCost) }
            item {
                FilledTonalButton(
                    onClick = { showAdd = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) { Text(text = "+  종목 추가", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            }
            if (summary.holdings.isEmpty()) {
                item {
                    Text(
                        text = "들고 있는 종목을 추가하면\n수익률과 보유일이 자동으로 보여요.",
                        fontSize = 14.sp, color = HoldSub, modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                items(summary.holdings, key = { it.id }) { h ->
                    HoldingCard(holding = h, onSell = { sellTarget = h }, modifier = Modifier.animateItem())
                }
            }
        }
    }

    if (showAdd) AddHoldingDialog(viewModel = viewModel, onClose = { showAdd = false })
    sellTarget?.let { h ->
        SellDialog(holding = h, onConfirm = { viewModel.sell(h); sellTarget = null }, onDismiss = { sellTarget = null })
    }
}

@Composable
private fun SummaryHeader(totalEval: Long, rate: Double, totalPnl: Long, totalCost: Long) {
    val tone = pnlColor(totalPnl)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HoldCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "총 평가금액", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HoldSub)
            Text(text = comma(totalEval) + "원", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = HoldInk)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 2.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(tone.copy(alpha = 0.14f)).padding(horizontal = 11.dp, vertical = 5.dp)) {
                    Text(text = "${signed(rate)}%", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = tone)
                }
                Text(text = "평가손익 ${formatWon(totalPnl)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = tone)
            }
            Text(text = "총 매수금액 ${comma(totalCost)}원", fontSize = 12.5.sp, color = HoldFaint, modifier = Modifier.padding(top = 2.dp))
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
        colors = CardDefaults.cardColors(containerColor = HoldCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = holding.ticker, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HoldInk)
                    HoldingBadge(days)
                }
                Text(text = "${signed(holding.returnRate)}%", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = tone)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "${comma(holding.buyPrice)} → ${comma(holding.currentPrice)}" + if (holding.quantity > 1) "  ·  ${holding.quantity}주" else "",
                        fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HoldSub,
                    )
                    Text(text = "평가손익 ${formatWon(holding.evalPnl)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = tone)
                    Text(text = "편입 ${holding.entryDate.format(DateTimeFormatter.ofPattern("MM/dd"))}", fontSize = 11.5.sp, color = HoldFaint)
                }
                OutlinedButton(onClick = onSell, shape = RoundedCornerShape(12.dp)) {
                    Text(text = "매도", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LossBlue)
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
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(text = "보유 종목 추가", fontWeight = FontWeight.Bold, color = HoldInk) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = input.ticker, onValueChange = viewModel::onTickerChange, singleLine = true, label = { Text("종목명") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = input.buyPriceText, onValueChange = viewModel::onBuyPriceChange, singleLine = true, label = { Text("매수가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = input.currentPriceText, onValueChange = viewModel::onCurrentPriceChange, singleLine = true, label = { Text("현재가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = input.quantityText, onValueChange = viewModel::onQuantityChange, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text(text = "편입일 · ${input.entryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}", color = HoldInk)
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
                    state.selectedDateMillis?.let { ms -> viewModel.onEntryDateChange(Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()) }
                    showDate = false
                }) { Text("확인") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("취소") } },
        ) { DatePicker(state = state) }
    }
}

@Composable
private fun SellDialog(holding: Holding, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "${holding.ticker} 매도", fontWeight = FontWeight.Bold, color = HoldInk) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "현재 평가손익 ${formatWon(holding.evalPnl)} (${signed(holding.returnRate)}%)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pnlColor(holding.evalPnl))
                Text(text = "매도하면 보유 목록에서 빠집니다.", fontSize = 13.sp, color = HoldSub)
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("매도", color = LossBlue) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } },
    )
}

private fun comma(v: Long): String {
    val sign = if (v < 0) "-" else ""
    val a = kotlin.math.abs(v).toString().reversed().chunked(3).joinToString(",").reversed()
    return "$sign$a"
}
private fun signed(rate: Double): String = "${if (rate >= 0) "+" else ""}${"%.2f".format(rate * 100)}"
