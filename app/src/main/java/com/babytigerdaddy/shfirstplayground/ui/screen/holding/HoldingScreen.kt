package com.babytigerdaddy.shfirstplayground.ui.screen.holding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.Account
import com.babytigerdaddy.shfirstplayground.domain.model.AllocationSlice
import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.DailyPnlPoint
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.CumulativeLineChart
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.LossBlue
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.ProfitRed
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.formatWon
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.pnlColor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 보유노트 톤 — 시인성 최우선
private val HoldBg = Color(0xFFEDEFF3)
private val HoldCard = Color(0xFFFFFFFF)
private val HoldInk = Color(0xFF0E1216)
private val HoldSub = Color(0xFF4B5562)
private val HoldFaint = Color(0xFF818B99)
private val HoldLine = Color(0xFFE2E6EC)
private val EntryBadgeBg = Color(0xFFEAE7FB)
private val EntryBadgeFg = Color(0xFF5A49C8)
private val DaysBadgeBg = Color(0xFFD7F0DF)
private val DaysBadgeFg = Color(0xFF15883F)

/** 보유노트 — 보유 중(표) / 판 내역(집계·그래프·매도 리스트) 토글 단독 앱. */
@Composable
fun HoldingScreen(viewModel: HoldingViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val sold by viewModel.soldHistory.collectAsStateWithLifecycle()
    val allocation by viewModel.allocation.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) } // 0=보유, 1=배분, 2=판내역
    var showAdd by remember { mutableStateOf(false) }
    var sellTarget by remember { mutableStateOf<Holding?>(null) }
    var editTarget by remember { mutableStateOf<Holding?>(null) }
    var delHolding by remember { mutableStateOf<Holding?>(null) }
    var delSold by remember { mutableStateOf<SoldRecord?>(null) }
    var showAddAccount by remember { mutableStateOf(false) }
    var showRenameAccount by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(HoldBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "보유노트", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = HoldInk)
                    AccountSelector(
                        accounts = accounts,
                        selectedId = selectedAccountId,
                        onSelect = viewModel::selectAccount,
                        onAddClick = { showAddAccount = true },
                        onRenameClick = { showRenameAccount = true },
                        onDeleteClick = { showDeleteAccount = true },
                    )
                }
            }
            item { SegToggle(tab = tab, onSelect = { tab = it }) }

            when (tab) {
                0 -> {
                    item { SummaryHeader(summary.totalEval, summary.totalReturnRate, summary.totalPnl, summary.totalCost) }
                    item {
                        FilledTonalButton(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                            Text(text = "+  종목 추가", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (summary.holdings.isEmpty()) {
                        item { Text(text = "들고 있는 종목을 추가하면\n표로 한눈에 보여요.", fontSize = 14.sp, color = HoldSub, modifier = Modifier.padding(top = 6.dp)) }
                    } else {
                        item { Text(text = "행 탭 → 편집 · 길게 눌러 삭제 · 평가손익·수익률은 세금·수수료 포함", fontSize = 11.sp, color = HoldFaint) }
                        item { HoldingTable(summary.holdings, onEdit = { editTarget = it }, onDelete = { delHolding = it }) }
                    }
                }
                1 -> allocationSection(allocation)
                else -> soldSection(sold, onDelete = { delSold = it })
            }
        }
    }

    if (showAdd) AddHoldingDialog(viewModel = viewModel, onClose = { showAdd = false })
    sellTarget?.let { h ->
        SellDialog(holding = h, onConfirm = { viewModel.sell(h); sellTarget = null }, onDismiss = { sellTarget = null })
    }
    editTarget?.let { h ->
        EditHoldingDialog(
            holding = h,
            onSave = { edited -> viewModel.updateHolding(edited); editTarget = null },
            onSellClick = { editTarget = null; sellTarget = h },
            onDismiss = { editTarget = null },
        )
    }
    delHolding?.let { h ->
        DeleteConfirmDialog(label = "${h.ticker} 보유", onConfirm = { viewModel.remove(h.id); delHolding = null }, onDismiss = { delHolding = null })
    }
    delSold?.let { r ->
        DeleteConfirmDialog(label = "${r.ticker} 매도 내역", onConfirm = { viewModel.deleteSoldRecord(r.id); delSold = null }, onDismiss = { delSold = null })
    }
    val curAccount = accounts.firstOrNull { it.id == selectedAccountId }
    if (showAddAccount) AccountNameDialog("새 계좌", "", onConfirm = { viewModel.addAccount(it); showAddAccount = false }, onDismiss = { showAddAccount = false })
    if (showRenameAccount) AccountNameDialog("계좌 이름 변경", curAccount?.name ?: "", onConfirm = { viewModel.renameAccount(selectedAccountId, it); showRenameAccount = false }, onDismiss = { showRenameAccount = false })
    if (showDeleteAccount) DeleteConfirmDialog(label = "'${curAccount?.name ?: "계좌"}' 계좌 (종목·판내역 포함)", onConfirm = { viewModel.deleteAccount(selectedAccountId); showDeleteAccount = false }, onDismiss = { showDeleteAccount = false })
}

@Composable
private fun SegToggle(tab: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0E3E9)).padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        listOf("보유 중", "배분", "판 내역").forEachIndexed { i, label ->
            val sel = i == tab
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (sel) HoldCard else Color.Transparent)
                    .clickable { onSelect(i) }.padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (sel) HoldInk else HoldSub)
            }
        }
    }
}

// ---------- 계좌 선택 ----------
@Composable
private fun AccountSelector(
    accounts: List<Account>,
    selectedId: String,
    onSelect: (String) -> Unit,
    onAddClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val isAll = selectedId == Account.ALL_ID
    val curIndex = accounts.indexOfFirst { it.id == selectedId }.coerceAtLeast(0)
    val curName = if (isAll) Account.ALL_NAME else accounts.getOrNull(curIndex)?.name ?: "내 계좌"
    val curColor = if (isAll) HoldSub else AllocColors[curIndex % AllocColors.size]
    Box {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(HoldCard).clickable { expanded = true }.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(curColor))
            Text(curName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HoldInk)
            Text("▾", fontSize = 13.sp, color = HoldSub)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // 전체 합산 (가상 계좌)
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(HoldSub))
                        Text("전체 (합산)", color = HoldInk, fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal)
                        if (isAll) Text("✓", color = LossBlue)
                    }
                },
                onClick = { onSelect(Account.ALL_ID); expanded = false },
            )
            HorizontalDivider()
            accounts.forEachIndexed { i, a ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(AllocColors[i % AllocColors.size]))
                            Text(a.name, color = HoldInk, fontWeight = if (a.id == selectedId) FontWeight.Bold else FontWeight.Normal)
                            if (a.id == selectedId) Text("✓", color = LossBlue)
                        }
                    },
                    onClick = { onSelect(a.id); expanded = false },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(text = { Text("+ 새 계좌 추가", color = LossBlue) }, onClick = { expanded = false; onAddClick() })
            if (!isAll) {
                DropdownMenuItem(text = { Text("현재 계좌 이름 변경", color = HoldInk) }, onClick = { expanded = false; onRenameClick() })
                if (accounts.size > 1) DropdownMenuItem(text = { Text("현재 계좌 삭제", color = ProfitRed) }, onClick = { expanded = false; onDeleteClick() })
            }
        }
    }
}

@Composable
private fun AccountNameDialog(title: String, initial: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HoldCard,
        titleContentColor = HoldInk,
        textContentColor = HoldSub,
        title = { Text(title, fontWeight = FontWeight.Bold, color = HoldInk) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.take(20) },
                singleLine = true,
                label = { Text("계좌 이름") },
                placeholder = { Text("가치투자 / 단타 / 내 계좌") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = { TextButton(onClick = { if (text.isNotBlank()) onConfirm(text.trim()) }, enabled = text.isNotBlank()) { Text("저장", color = LossBlue) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = HoldSub) } },
    )
}

// ---------- 보유 중 (표) ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HoldingTable(holdings: List<Holding>, onEdit: (Holding) -> Unit, onDelete: (Holding) -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = HoldCard), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            // 헤더 (2단)
            Row(Modifier.fillMaxWidth().background(Color(0xFFF3F5F8)).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1.5f)) { Text("종목명", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HoldSub) }
                TwoLineHead("매입가", "현재가", 1.4f)
                TwoLineHead("수량", "보유일", 1.0f)
                TwoLineHead("평가손익", "수익률", 1.6f)
            }
            holdings.forEachIndexed { idx, h ->
                if (idx > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(HoldLine))
                val tone = pnlColor(h.netEvalPnl)
                val days = h.holdingDays(LocalDate.now())
                Row(
                    Modifier.fillMaxWidth()
                        .combinedClickable(onClick = { onEdit(h) }, onLongClick = { onDelete(h) })
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1.5f)) { Text(h.ticker, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HoldInk) }
                    // 매입가 / 현재가
                    Column(Modifier.weight(1.4f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(comma(h.buyPrice), fontSize = 13.sp, color = HoldSub)
                        Text(comma(h.currentPrice), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HoldInk)
                    }
                    // 수량 / 보유일 배지
                    Column(Modifier.weight(1.0f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("${h.quantity}", fontSize = 13.sp, color = HoldInk)
                        val (bg, fg, lab) = if (days <= 0L) Triple(EntryBadgeBg, EntryBadgeFg, "진입") else Triple(DaysBadgeBg, DaysBadgeFg, "${days}일")
                        Box(Modifier.clip(RoundedCornerShape(6.dp)).background(bg).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(lab, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fg)
                        }
                    }
                    // 평가손익 / 수익률 (세금·수수료 포함 = 세후)
                    Column(Modifier.weight(1.6f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(formatWon(h.netEvalPnl), fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = tone)
                        Text("${signed(h.netReturnRate)}%", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = tone)
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TwoLineHead(top: String, bottom: String, weight: Float) {
    Column(Modifier.weight(weight), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(top, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HoldSub)
        Text(bottom, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HoldSub)
    }
}

// ---------- 판 내역 ----------
private fun androidx.compose.foundation.lazy.LazyListScope.soldSection(sold: SoldHistorySummary, onDelete: (SoldRecord) -> Unit) {
    if (sold.records.isEmpty()) {
        item { Text(text = "아직 판 종목이 없어요.\n보유 표에서 종목을 매도하면 여기 내역으로 쌓여요.", fontSize = 14.sp, color = HoldSub, modifier = Modifier.padding(top = 6.dp)) }
        return
    }
    item {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox("총 실현손익", formatWon(sold.totalRealized), pnlColor(sold.totalRealized), Modifier.weight(1.3f))
            StatBox("평균 수익률", "${signed(sold.avgReturnRate)}%", if (sold.avgReturnRate > 0) ProfitRed else if (sold.avgReturnRate < 0) LossBlue else HoldInk, Modifier.weight(1f))
            StatBox("승률", "${(sold.winRate * 100).toInt()}%", HoldInk, Modifier.weight(1f), sub = "${sold.winCount}/${sold.saleCount}")
        }
    }
    item {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = HoldCard), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("누적 실현손익", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HoldInk)
                if (sold.cumulative.size < 2) {
                    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        Text("두 번 이상 팔면 곡선이 그려져요", fontSize = 13.sp, color = HoldSub)
                    }
                } else {
                    CumulativeLineChart(
                        daily = sold.cumulative.map { DailyPnlPoint(it.soldDate, it.realizedPnl, it.cumulative, "") },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                    )
                }
            }
        }
    }
    sold.bestSale?.let { b ->
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ProfitRed.copy(alpha = 0.10f))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("🏆 베스트 매도", fontSize = 12.sp, color = HoldSub)
                        Text("${b.ticker} · ${b.heldDays}일 보유", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HoldInk)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatWon(b.realizedPnl), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ProfitRed)
                        Text("${signed(b.returnRate)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ProfitRed)
                    }
                }
            }
        }
    }
    item { Text("매도 내역  (길게 눌러 삭제)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HoldInk, modifier = Modifier.padding(top = 2.dp)) }
    items(sold.records, key = { it.id }) { r -> SoldRow(r, onDelete = onDelete) }
}

@Composable
private fun StatBox(title: String, value: String, accent: Color, modifier: Modifier = Modifier, sub: String? = null) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = HoldCard), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, fontSize = 11.5.sp, color = HoldSub)
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            if (sub != null) Text(sub, fontSize = 10.5.sp, color = HoldFaint)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SoldRow(r: SoldRecord, onDelete: (SoldRecord) -> Unit) {
    val tone = pnlColor(r.netRealizedPnl)
    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = {}, onLongClick = { onDelete(r) }),
        shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = HoldCard), elevation = CardDefaults.cardElevation(1.5.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(r.ticker, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HoldInk)
                    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(HoldLine).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("${r.heldDays}일 보유", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HoldSub)
                    }
                }
                Text("${comma(r.buyPrice)} → ${comma(r.sellPrice)}  ·  ${r.soldDate.format(DateTimeFormatter.ofPattern("M/d"))} 매도", fontSize = 12.sp, color = HoldFaint)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(formatWon(r.netRealizedPnl), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = tone)
                Text("${signed(r.netReturnRate)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tone)
            }
        }
    }
}

// ---------- 자산 배분 (원 그래프) ----------
private val AllocColors = listOf(
    Color(0xFF2E9E88), Color(0xFF3B6FB0), Color(0xFFD98C2B), Color(0xFF8A5CC0),
    Color(0xFFCB5B7A), Color(0xFF4B9E5F), Color(0xFF5C6B7A), Color(0xFF2FA6C4),
)

private fun androidx.compose.foundation.lazy.LazyListScope.allocationSection(alloc: AssetAllocation) {
    if (alloc.slices.isEmpty()) {
        item { Text(text = "보유 종목이 없어요.\n종목을 담으면 어디에 얼마나 쏠렸는지 원으로 보여줘요.", fontSize = 14.sp, color = HoldSub, modifier = Modifier.padding(top = 6.dp)) }
        return
    }
    item {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = HoldCard), elevation = CardDefaults.cardElevation(3.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                AllocationDonut(alloc.slices, Modifier.size(212.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("총 평가금액", fontSize = 12.sp, color = HoldSub)
                    Text(comma(alloc.totalEval) + "원", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = HoldInk)
                }
            }
        }
    }
    if (alloc.concentrationTicker != null) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ProfitRed.copy(alpha = 0.10f))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("⚠", fontSize = 18.sp)
                    Text(buildString { append(alloc.concentrationTicker); append("에 ") },
                        fontSize = 14.sp, color = HoldInk, fontWeight = FontWeight.Bold)
                    Text("${(alloc.concentrationRatio * 100).toInt()}% 쏠림", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ProfitRed)
                    Text("· 집중 종목", fontSize = 12.sp, color = HoldSub)
                }
            }
        }
    }
    item { Text("종목별 비중", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HoldInk, modifier = Modifier.padding(top = 2.dp)) }
    itemsIndexed(alloc.slices) { i, s -> AllocationLegendRow(i, s) }
}

@Composable
private fun AllocationDonut(slices: List<AllocationSlice>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.17f
        val d = size.minDimension - stroke
        val tl = Offset((size.width - d) / 2f, (size.height - d) / 2f)
        var start = -90f
        slices.forEachIndexed { i, s ->
            val sweep = (s.ratio * 360.0).toFloat()
            if (sweep > 0f) {
                drawArc(
                    color = AllocColors[i % AllocColors.size],
                    startAngle = start,
                    sweepAngle = (sweep - 1.4f).coerceAtLeast(0.6f),
                    useCenter = false,
                    topLeft = tl,
                    size = Size(d, d),
                    style = Stroke(width = stroke),
                )
            }
            start += sweep
        }
    }
}

@Composable
private fun AllocationLegendRow(index: Int, s: AllocationSlice) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(AllocColors[index % AllocColors.size]))
            Text(s.ticker, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HoldInk)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${(s.ratio * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = HoldInk)
            Text(comma(s.evalAmount) + "원", fontSize = 12.sp, color = HoldFaint)
        }
    }
}

// ---------- 보유 요약 헤더 ----------
@Composable
private fun SummaryHeader(totalEval: Long, rate: Double, totalPnl: Long, totalCost: Long) {
    val tone = pnlColor(totalPnl)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = HoldCard), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("총 평가금액", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HoldSub)
            Text(comma(totalEval) + "원", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = HoldInk)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.clip(RoundedCornerShape(9.dp)).background(tone.copy(alpha = 0.14f)).padding(horizontal = 11.dp, vertical = 5.dp)) {
                    Text("${signed(rate)}%", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = tone)
                }
                Text("평가손익 ${formatWon(totalPnl)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = tone)
            }
            Text("총 매수금액 ${comma(totalCost)}원", fontSize = 12.5.sp, color = HoldFaint)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHoldingDialog(viewModel: HoldingViewModel, onClose: () -> Unit) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    val candidates by viewModel.stockCandidates.collectAsStateWithLifecycle()
    var showDate by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = HoldCard,
        titleContentColor = HoldInk,
        textContentColor = HoldSub,
        title = { Text("보유 종목 추가", fontWeight = FontWeight.Bold, color = HoldInk) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input.ticker,
                    onValueChange = { viewModel.onTickerChange(it); viewModel.searchStock(it) },
                    singleLine = true,
                    label = { Text("종목명 (이름으로 검색)") },
                    suffix = { if (input.codeText.isNotBlank()) Text(input.codeText, color = HoldFaint) },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (candidates.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFF3F5F8))) {
                        candidates.take(6).forEach { s ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.selectStock(s) }.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(s.name, fontSize = 14.sp, color = HoldInk)
                                Text("${s.code} · ${s.market}", fontSize = 11.sp, color = HoldFaint)
                            }
                        }
                    }
                }
                OutlinedTextField(input.buyPriceText, viewModel::onBuyPriceChange, singleLine = true, label = { Text("매수가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(input.currentPriceText, viewModel::onCurrentPriceChange, singleLine = true, label = { Text("현재가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(input.quantityText, viewModel::onQuantityChange, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("편입일 · ${input.entryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}", color = HoldInk)
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
            confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { ms -> viewModel.onEntryDateChange(Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()) }; showDate = false }) { Text("확인") } },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("취소") } },
        ) { DatePicker(state = state) }
    }
}

@Composable
private fun SellDialog(holding: Holding, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HoldCard,
        titleContentColor = HoldInk,
        textContentColor = HoldSub,
        title = { Text("${holding.ticker} 매도", fontWeight = FontWeight.Bold, color = HoldInk) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("현재 평가손익 ${formatWon(holding.evalPnl)} (${signed(holding.returnRate)}%)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pnlColor(holding.evalPnl))
                Text("매도하면 '판 내역'으로 넘어갑니다.", fontSize = 13.sp, color = HoldSub)
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("매도", color = LossBlue) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = HoldSub) } },
    )
}

@Composable
private fun DeleteConfirmDialog(label: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HoldCard,
        titleContentColor = HoldInk,
        textContentColor = HoldSub,
        title = { Text("삭제", fontWeight = FontWeight.Bold, color = HoldInk) },
        text = { Text("'$label'을(를) 삭제할까요? 되돌릴 수 없어요.", fontSize = 14.sp, color = HoldSub) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("삭제", color = LossBlue) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = HoldSub) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditHoldingDialog(holding: Holding, onSave: (Holding) -> Unit, onSellClick: () -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(holding.ticker) }
    var buy by remember { mutableStateOf(holding.buyPrice.toString()) }
    var current by remember { mutableStateOf(holding.currentPrice.toString()) }
    var qty by remember { mutableStateOf(holding.quantity.toString()) }
    var entryDate by remember { mutableStateOf(holding.entryDate) }
    var showDate by remember { mutableStateOf(false) }
    val canSave = name.isNotBlank() && buy.any { it.isDigit() } && current.any { it.isDigit() }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HoldCard,
        titleContentColor = HoldInk,
        textContentColor = HoldSub,
        title = { Text("종목 편집", fontWeight = FontWeight.Bold, color = HoldInk) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it.take(20) }, singleLine = true, label = { Text("종목명") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(buy, { buy = it.filter { c -> c.isDigit() }.take(12) }, singleLine = true, label = { Text("매수가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(current, { current = it.filter { c -> c.isDigit() }.take(12) }, singleLine = true, label = { Text("현재가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(qty, { qty = it.filter { c -> c.isDigit() }.take(9) }, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("편입일 · ${entryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}", color = HoldInk)
                }
                OutlinedButton(onClick = onSellClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("이 종목 매도 (판 내역으로)", color = LossBlue)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val b = buy.filter { it.isDigit() }.toLongOrNull() ?: return@TextButton
                    val c = current.filter { it.isDigit() }.toLongOrNull() ?: return@TextButton
                    val q = qty.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    onSave(holding.copy(ticker = name.trim(), buyPrice = b, currentPrice = c, quantity = q, entryDate = entryDate))
                },
                enabled = canSave,
            ) { Text("저장", color = LossBlue) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = HoldSub) } },
    )
    if (showDate) {
        val state = rememberDatePickerState(initialSelectedDateMillis = entryDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { ms -> entryDate = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate() }; showDate = false }) { Text("확인") } },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("취소") } },
        ) { DatePicker(state = state) }
    }
}

private fun comma(v: Long): String {
    val sign = if (v < 0) "-" else ""
    return "$sign${kotlin.math.abs(v).toString().reversed().chunked(3).joinToString(",").reversed()}"
}
private fun signed(rate: Double): String = "${if (rate >= 0) "+" else ""}${"%.2f".format(rate * 100)}"
/** 표 컴팩트 표시 — 원을 만원 단위로(514,000 → 51.4만). */
private fun manwon(v: Long): String = "%.1f만".format(v / 10000.0)
