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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.R
import com.babytigerdaddy.shfirstplayground.domain.model.Account
import com.babytigerdaddy.shfirstplayground.domain.model.AllocationSlice
import com.babytigerdaddy.shfirstplayground.domain.model.AssetAllocation
import com.babytigerdaddy.shfirstplayground.domain.model.DailyPnlPoint
import com.babytigerdaddy.shfirstplayground.domain.model.Holding
import com.babytigerdaddy.shfirstplayground.domain.model.SoldHistorySummary
import com.babytigerdaddy.shfirstplayground.domain.model.SoldRecord
import com.babytigerdaddy.shfirstplayground.domain.usecase.SoldRecordCalculator
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.CumulativeLineChart
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.LossBlue
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.ProfitRed
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.formatWon
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.pnlColor
import com.babytigerdaddy.shfirstplayground.ui.screen.settings.SettingsScreen
import com.babytigerdaddy.shfirstplayground.ui.theme.LocalHoldingColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 보유노트 톤 — 시인성 최우선
private val EntryBadgeBg = Color(0xFFEAE7FB)
private val EntryBadgeFg = Color(0xFF5A49C8)
private val DaysBadgeBg = Color(0xFFD7F0DF)
private val DaysBadgeFg = Color(0xFF15883F)

/** 보유노트 — 하단 4탭(보유 / 배분 / 판내역 / 설정) 단독 앱. */
@Composable
fun HoldingScreen(viewModel: HoldingViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val sold by viewModel.soldHistory.collectAsStateWithLifecycle()
    val allocation by viewModel.allocation.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    val priceRefresh by viewModel.priceRefresh.collectAsStateWithLifecycle()
    val masterSync by viewModel.masterSync.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) } // 0=보유 1=배분 2=판내역 3=설정
    var showAdd by remember { mutableStateOf(false) }
    var sellTarget by remember { mutableStateOf<Holding?>(null) }
    var editTarget by remember { mutableStateOf<Holding?>(null) }
    var delHolding by remember { mutableStateOf<Holding?>(null) }
    var delSold by remember { mutableStateOf<SoldRecord?>(null) }
    var editSold by remember { mutableStateOf<SoldRecord?>(null) }
    var showAddAccount by remember { mutableStateOf(false) }
    var showRenameAccount by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    val c = LocalHoldingColors.current

    // 앱 진입 시 저장된 종목 현재가를 야후에서 자동 갱신
    LaunchedEffect(Unit) { viewModel.refreshPrices() }

    Scaffold(
        containerColor = c.bg,
        bottomBar = { HoldingBottomBar(tab = tab, onSelect = { tab = it }) },
    ) { inner ->
        if (tab == 3) {
            SettingsScreen(embedded = true, modifier = Modifier.padding(inner))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner).padding(horizontal = 18.dp),
                contentPadding = PaddingValues(top = 18.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    AccountSelector(
                        accounts = accounts,
                        selectedId = selectedAccountId,
                        onSelect = viewModel::selectAccount,
                        onAddClick = { showAddAccount = true },
                        onRenameClick = { showRenameAccount = true },
                        onDeleteClick = { showDeleteAccount = true },
                    )
                }
                if ((masterSync.loading && masterSync.firstLoad) || masterSync.updatedCount > 0 || masterSync.failed) {
                    item { MasterSyncBar(masterSync) }
                }
                item { RefreshBar(priceRefresh, onRefresh = { viewModel.refreshPrices() }) }

                when (tab) {
                    0 -> {
                        item { SummaryHeader(summary.totalEval, summary.totalReturnRate, summary.totalPnl, summary.totalCost) }
                        item {
                            FilledTonalButton(
                                onClick = { showAdd = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = c.pointBg,
                                    contentColor = c.point,
                                ),
                            ) {
                                Text(text = "+  종목 추가", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (summary.holdings.isEmpty()) {
                            item { Text(text = "들고 있는 종목을 추가하면\n표로 한눈에 보여요.", fontSize = 14.sp, color = c.sub, modifier = Modifier.padding(top = 6.dp)) }
                        } else {
                            item { Text(text = "행 탭 → 편집 · 길게 눌러 삭제", fontSize = 11.sp, color = c.faint) }
                            item { HoldingTable(summary.holdings, onEdit = { editTarget = it }, onDelete = { delHolding = it }) }
                        }
                    }
                    1 -> allocationSection(allocation)
                    else -> soldSection(sold, onEdit = { editSold = it }, onDelete = { delSold = it })
                }
            }
        }
    }

    if (showAdd) AddHoldingDialog(viewModel = viewModel, onClose = { showAdd = false })
    sellTarget?.let { h ->
        SellDialog(holding = h, onConfirm = { price, qty -> viewModel.sell(h, price, qty); sellTarget = null }, onDismiss = { sellTarget = null })
    }
    editTarget?.let { h ->
        EditHoldingDialog(
            holding = h,
            viewModel = viewModel,
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
    editSold?.let { r ->
        EditSoldDialog(record = r, onSave = { edited -> viewModel.updateSoldRecord(edited); editSold = null }, onDismiss = { editSold = null })
    }
    val curAccount = accounts.firstOrNull { it.id == selectedAccountId }
    if (showAddAccount) AccountNameDialog("새 계좌", "", onConfirm = { viewModel.addAccount(it); showAddAccount = false }, onDismiss = { showAddAccount = false })
    if (showRenameAccount) AccountNameDialog("계좌 이름 변경", curAccount?.name ?: "", onConfirm = { viewModel.renameAccount(selectedAccountId, it); showRenameAccount = false }, onDismiss = { showRenameAccount = false })
    if (showDeleteAccount) DeleteConfirmDialog(label = "'${curAccount?.name ?: "계좌"}' 계좌 (종목·판내역 포함)", onConfirm = { viewModel.deleteAccount(selectedAccountId); showDeleteAccount = false }, onDismiss = { showDeleteAccount = false })
}

@Composable
private fun HoldingBottomBar(tab: Int, onSelect: (Int) -> Unit) {
    val c = LocalHoldingColors.current
    val items = listOf(
        R.drawable.ic_tab_holding to "보유",
        R.drawable.ic_tab_allocation to "배분",
        R.drawable.ic_tab_sold to "판내역",
        R.drawable.ic_tab_settings to "설정",
    )
    NavigationBar(containerColor = c.card, tonalElevation = 0.dp) {
        items.forEachIndexed { i, (iconRes, label) ->
            NavigationBarItem(
                selected = i == tab,
                onClick = { onSelect(i) },
                icon = { Icon(painterResource(iconRes), contentDescription = label, modifier = Modifier.size(24.dp)) },
                label = { Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = c.point,
                    selectedTextColor = c.point,
                    indicatorColor = c.pointBg,
                    unselectedIconColor = c.sub,
                    unselectedTextColor = c.sub,
                ),
            )
        }
    }
}

// ---------- 종목 목록 동기화 바 ----------
@Composable
private fun MasterSyncBar(state: MasterSyncState) {
    val c = LocalHoldingColors.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        when {
            state.loading && state.firstLoad -> {
                CircularProgressIndicator(modifier = Modifier.size(13.dp), strokeWidth = 2.dp, color = c.sub)
                Text("종목 목록 준비 중... (처음 한 번)", fontSize = 12.sp, color = c.sub)
            }
            state.failed -> Text("⚠ 종목 목록 갱신 실패 · 기본 종목으로 검색", fontSize = 12.sp, color = Color(0xFFC8881A))
            state.updatedCount > 0 -> Text("종목 목록 최신 · ${state.updatedCount}종목 반영", fontSize = 12.sp, color = Color(0xFF15883F))
        }
    }
}

// ---------- 시세 갱신 바 ----------
@Composable
private fun RefreshBar(state: PriceRefreshState, onRefresh: () -> Unit) {
    val c = LocalHoldingColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            when {
                state.loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(13.dp), strokeWidth = 2.dp, color = c.sub)
                    Text("시세 업데이트 중...", fontSize = 12.sp, color = c.sub)
                }
                state.failed -> Text("⚠ 갱신 실패 · 마지막 값 유지", fontSize = 12.sp, color = Color(0xFFC8881A))
                state.lastUpdated != null -> Text(
                    "방금 갱신 · ${state.lastUpdated.format(DateTimeFormatter.ofPattern("HH:mm"))}" + if (state.lastFetchedCount > 0) " · ${state.lastFetchedCount}종목" else "",
                    fontSize = 12.sp, color = c.sub,
                )
                else -> Text("새로고침으로 현재가 불러오기", fontSize = 12.sp, color = c.faint)
            }
        }
        Text(
            text = "↻ 새로고침",
            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.point,
            modifier = Modifier.clip(MaterialTheme.shapes.small).clickable(enabled = !state.loading) { onRefresh() }.padding(horizontal = 8.dp, vertical = 4.dp),
        )
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
    val c = LocalHoldingColors.current
    var expanded by remember { mutableStateOf(false) }
    val isAll = selectedId == Account.ALL_ID
    val curIndex = accounts.indexOfFirst { it.id == selectedId }.coerceAtLeast(0)
    val curName = if (isAll) Account.ALL_NAME else accounts.getOrNull(curIndex)?.name ?: "내 계좌"
    val curColor = if (isAll) c.sub else AllocColors[curIndex % AllocColors.size]
    Box {
        Row(
            modifier = Modifier.clip(MaterialTheme.shapes.medium).background(c.card).clickable { expanded = true }.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(curColor))
            Text(curName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.ink)
            Text("▾", fontSize = 13.sp, color = c.sub)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // 전체 합산 (가상 계좌)
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(c.sub))
                        Text("전체 (합산)", color = c.ink, fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal)
                        if (isAll) Text("✓", color = c.point)
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
                            Text(a.name, color = c.ink, fontWeight = if (a.id == selectedId) FontWeight.Bold else FontWeight.Normal)
                            if (a.id == selectedId) Text("✓", color = c.point)
                        }
                    },
                    onClick = { onSelect(a.id); expanded = false },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(text = { Text("+ 새 계좌 추가", color = c.point) }, onClick = { expanded = false; onAddClick() })
            if (!isAll) {
                DropdownMenuItem(text = { Text("현재 계좌 이름 변경", color = c.ink) }, onClick = { expanded = false; onRenameClick() })
                if (accounts.size > 1) DropdownMenuItem(text = { Text("현재 계좌 삭제", color = ProfitRed) }, onClick = { expanded = false; onDeleteClick() })
            }
        }
    }
}

@Composable
private fun AccountNameDialog(title: String, initial: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    val c = LocalHoldingColors.current
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.card,
        titleContentColor = c.ink,
        textContentColor = c.sub,
        title = { Text(title, fontWeight = FontWeight.Bold, color = c.ink) },
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
        confirmButton = { TextButton(onClick = { if (text.isNotBlank()) onConfirm(text.trim()) }, enabled = text.isNotBlank()) { Text("저장", color = c.point) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = c.sub) } },
    )
}

// ---------- 보유 중 (표) ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HoldingTable(holdings: List<Holding>, onEdit: (Holding) -> Unit, onDelete: (Holding) -> Unit) {
    val c = LocalHoldingColors.current
    Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            // 헤더 (2단) — 은은한 배경 띠(내용과 구분) + 항목 사이 세로 구분선
            Row(Modifier.fillMaxWidth().background(c.line).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1.5f)) { Text("종목명", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = c.sub) }
                Box(Modifier.size(width = 1.dp, height = 26.dp).background(c.faint.copy(alpha = 0.35f)))
                TwoLineHead("매입가", "현재가", 1.4f)
                Box(Modifier.size(width = 1.dp, height = 26.dp).background(c.faint.copy(alpha = 0.35f)))
                TwoLineHead("수량", "보유일", 1.0f)
                Box(Modifier.size(width = 1.dp, height = 26.dp).background(c.faint.copy(alpha = 0.35f)))
                TwoLineHead("평가손익", "수익률", 1.6f)
            }
            holdings.forEachIndexed { idx, h ->
                if (idx > 0) Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(1.dp).background(c.faint.copy(alpha = 0.22f)))
                val tone = pnlColor(h.evalPnl)
                val days = h.holdingDays(LocalDate.now())
                Row(
                    Modifier.fillMaxWidth()
                        .combinedClickable(onClick = { onEdit(h) }, onLongClick = { onDelete(h) })
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1.5f)) { Text(h.ticker, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.ink) }
                    // 매입가 / 현재가
                    Column(Modifier.weight(1.4f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(comma(h.buyPrice), fontSize = 13.sp, color = c.sub)
                        Text(comma(h.currentPrice), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = c.ink)
                    }
                    // 수량 / 보유일 배지
                    Column(Modifier.weight(1.0f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("${h.quantity}", fontSize = 13.sp, color = c.ink)
                        val (bg, fg, lab) = if (days <= 0L) Triple(EntryBadgeBg, EntryBadgeFg, "진입") else Triple(DaysBadgeBg, DaysBadgeFg, "${days}일")
                        Box(Modifier.clip(MaterialTheme.shapes.small).background(bg).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(lab, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fg)
                        }
                    }
                    // 평가손익 / 수익률 (세전 — 증권사 잔고 화면과 동일)
                    Column(Modifier.weight(1.6f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(formatWon(h.evalPnl), fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = tone)
                        Text("${signed(h.returnRate)}%", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = tone)
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TwoLineHead(top: String, bottom: String, weight: Float) {
    val c = LocalHoldingColors.current
    Column(Modifier.weight(weight), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(top, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = c.sub)
        Text(bottom, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = c.sub)
    }
}

// ---------- 판 내역 ----------
private fun androidx.compose.foundation.lazy.LazyListScope.soldSection(sold: SoldHistorySummary, onEdit: (SoldRecord) -> Unit, onDelete: (SoldRecord) -> Unit) {
    if (sold.records.isEmpty()) {
        item {
            val c = LocalHoldingColors.current
            Text(text = "아직 판 종목이 없어요.\n보유 표에서 종목을 매도하면 여기 내역으로 쌓여요.", fontSize = 14.sp, color = c.sub, modifier = Modifier.padding(top = 6.dp))
        }
        return
    }
    item {
        val c = LocalHoldingColors.current
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // 실현손익 — 좌우 스와이프로 전체/이번주/이번달 전환
            RealizedSwipeCard(SoldRecordCalculator.realizedByPeriod(sold.records, LocalDate.now()))
            Row(Modifier.height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox("평균 수익률", "${signed(sold.avgReturnRate)}%", if (sold.avgReturnRate > 0) ProfitRed else if (sold.avgReturnRate < 0) LossBlue else c.ink, Modifier.weight(1f).fillMaxHeight())
                StatBox("승률", "${(sold.winRate * 100).toInt()}%", c.ink, Modifier.weight(1f).fillMaxHeight(), sub = "${sold.winCount}/${sold.saleCount}")
            }
        }
    }
    item {
        val c = LocalHoldingColors.current
        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("누적 실현손익 추이", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = c.ink)
                if (sold.cumulative.size < 2) {
                    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        Text("두 번 이상 팔면 곡선이 그려져요", fontSize = 13.sp, color = c.sub)
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
            val c = LocalHoldingColors.current
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = ProfitRed.copy(alpha = 0.10f))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("🏆 베스트 매도", fontSize = 12.sp, color = c.sub)
                        Text("${b.ticker} · ${b.heldDays}일 보유", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.ink)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatWon(b.realizedPnl), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ProfitRed)
                        Text("${signed(b.returnRate)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ProfitRed)
                    }
                }
            }
        }
    }
    item {
        val c = LocalHoldingColors.current
        Text("매도 내역  (길게 눌러 삭제)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = c.ink, modifier = Modifier.padding(top = 2.dp))
    }
    items(sold.records, key = { it.id }) { r -> SoldRow(r, onEdit = onEdit, onDelete = onDelete) }
}

@Composable
private fun StatBox(title: String, value: String, accent: Color, modifier: Modifier = Modifier, sub: String? = null) {
    val c = LocalHoldingColors.current
    Card(modifier = modifier, shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, fontSize = 11.5.sp, color = c.sub)
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            if (sub != null) Text(sub, fontSize = 10.5.sp, color = c.faint)
        }
    }
}

// ---------- 실현손익 스와이프 카드 (좌우로 전체 / 이번주 / 이번달 전환) ----------
@Composable
private fun RealizedSwipeCard(byPeriod: com.babytigerdaddy.shfirstplayground.domain.usecase.RealizedByPeriod) {
    val c = LocalHoldingColors.current
    val pages = listOf(
        Triple("전체 실현손익", byPeriod.allRealized, byPeriod.allCount),
        Triple("이번주 실현손익", byPeriod.weekRealized, byPeriod.weekCount),
        Triple("이번달 실현손익", byPeriod.monthRealized, byPeriod.monthCount),
    )
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { pages.size })
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            androidx.compose.foundation.pager.HorizontalPager(state = pagerState) { page ->
                val (title, realized, count) = pages[page]
                Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(title, fontSize = 12.sp, color = c.sub)
                    Text(formatWon(realized), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = pnlColor(realized))
                    Text("${count}건", fontSize = 12.sp, color = c.faint)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(pages.size) { i ->
                    val on = i == pagerState.currentPage
                    Box(Modifier.padding(horizontal = 3.dp).size(if (on) 7.dp else 6.dp).clip(RoundedCornerShape(4.dp)).background(if (on) c.point else c.line))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SoldRow(r: SoldRecord, onEdit: (SoldRecord) -> Unit, onDelete: (SoldRecord) -> Unit) {
    val c = LocalHoldingColors.current
    val tone = pnlColor(r.realizedPnl)
    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = { onEdit(r) }, onLongClick = { onDelete(r) }),
        shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(1.5.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(r.ticker, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.ink)
                    Box(Modifier.clip(MaterialTheme.shapes.small).background(c.line).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("${r.heldDays}일 보유", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = c.sub)
                    }
                }
                Text("${comma(r.buyPrice)} → ${comma(r.sellPrice)}  ·  ${r.soldDate.format(DateTimeFormatter.ofPattern("M/d"))} 매도", fontSize = 12.sp, color = c.faint)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(formatWon(r.realizedPnl), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = tone)
                Text("${signed(r.returnRate)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tone)
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
        item {
            val c = LocalHoldingColors.current
            Text(text = "보유 종목이 없어요.\n종목을 담으면 어디에 얼마나 쏠렸는지 원으로 보여줘요.", fontSize = 14.sp, color = c.sub, modifier = Modifier.padding(top = 6.dp))
        }
        return
    }
    item {
        val c = LocalHoldingColors.current
        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(3.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                AllocationDonut(alloc.slices, Modifier.size(212.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("총 평가금액", fontSize = 12.sp, color = c.sub)
                    Text(comma(alloc.totalEval) + "원", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
                }
            }
        }
    }
    if (alloc.concentrationTicker != null) {
        item {
            val c = LocalHoldingColors.current
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = ProfitRed.copy(alpha = 0.10f))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("⚠", fontSize = 18.sp)
                    Text(buildString { append(alloc.concentrationTicker); append("에 ") },
                        fontSize = 14.sp, color = c.ink, fontWeight = FontWeight.Bold)
                    Text("${(alloc.concentrationRatio * 100).toInt()}% 쏠림", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ProfitRed)
                    Text("· 집중 종목", fontSize = 12.sp, color = c.sub)
                }
            }
        }
    }
    item {
        val c = LocalHoldingColors.current
        Text("종목별 비중", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = c.ink, modifier = Modifier.padding(top = 2.dp))
    }
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
    val c = LocalHoldingColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(AllocColors[index % AllocColors.size]))
            Text(s.ticker, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.ink)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${(s.ratio * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
            Text(comma(s.evalAmount) + "원", fontSize = 12.sp, color = c.faint)
        }
    }
}

// ---------- 보유 요약 헤더 ----------
@Composable
private fun SummaryHeader(totalEval: Long, rate: Double, totalPnl: Long, totalCost: Long) {
    val c = LocalHoldingColors.current
    val tone = pnlColor(totalPnl)
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = c.card), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("총 평가금액", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = c.sub)
            Text(comma(totalEval) + "원", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.clip(MaterialTheme.shapes.small).background(tone.copy(alpha = 0.14f)).padding(horizontal = 11.dp, vertical = 5.dp)) {
                    Text("${signed(rate)}%", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = tone)
                }
                Text("평가손익 ${formatWon(totalPnl)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = tone)
            }
            Text("총 매수금액 ${comma(totalCost)}원", fontSize = 12.5.sp, color = c.faint)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHoldingDialog(viewModel: HoldingViewModel, onClose: () -> Unit) {
    val c = LocalHoldingColors.current
    val input by viewModel.input.collectAsStateWithLifecycle()
    val candidates by viewModel.stockCandidates.collectAsStateWithLifecycle()
    var showDate by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = c.card,
        titleContentColor = c.ink,
        textContentColor = c.sub,
        title = { Text("보유 종목 추가", fontWeight = FontWeight.Bold, color = c.ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input.ticker,
                    onValueChange = { viewModel.onTickerChange(it); viewModel.searchStock(it) },
                    singleLine = true,
                    label = { Text("종목명 (이름으로 검색)") },
                    suffix = { if (input.codeText.isNotBlank()) Text(input.codeText, color = c.faint) },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (candidates.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).background(c.line)) {
                        candidates.take(6).forEach { s ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.selectStock(s) }.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(s.name, fontSize = 14.sp, color = c.ink)
                                Text("${s.code} · ${s.market}", fontSize = 11.sp, color = c.faint)
                            }
                        }
                    }
                }
                OutlinedTextField(input.buyPriceText, viewModel::onBuyPriceChange, singleLine = true, label = { Text("매수가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(input.quantityText, viewModel::onQuantityChange, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Text("현재가는 앱이 야후에서 자동으로 받아와요.", fontSize = 11.sp, color = c.faint)
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                    Text("편입일 · ${input.entryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}", color = c.ink)
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
private fun SellDialog(holding: Holding, onConfirm: (sellPrice: Long, quantity: Int) -> Unit, onDismiss: () -> Unit) {
    val c = LocalHoldingColors.current
    var sellPriceText by remember { mutableStateOf(holding.currentPrice.toString()) }
    var qtyText by remember { mutableStateOf(holding.quantity.toString()) }
    val sellPrice = sellPriceText.filter { it.isDigit() }.toLongOrNull() ?: holding.currentPrice
    val qty = (qtyText.filter { it.isDigit() }.toIntOrNull() ?: holding.quantity).coerceIn(1, holding.quantity)
    val realized = (sellPrice - holding.buyPrice) * qty
    val rate = if (holding.buyPrice > 0) (sellPrice - holding.buyPrice).toDouble() / holding.buyPrice else 0.0
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.card,
        titleContentColor = c.ink,
        textContentColor = c.sub,
        title = { Text("${holding.ticker} 매도", fontWeight = FontWeight.Bold, color = c.ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(sellPriceText, { sellPriceText = it.filter { d -> d.isDigit() }.take(12) }, singleLine = true, label = { Text("매도가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(qtyText, { qtyText = it.filter { d -> d.isDigit() }.take(9) }, singleLine = true, label = { Text("수량 (보유 ${holding.quantity}주)") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Text("실현손익 ${formatWon(realized)}  (${signed(rate)}%)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pnlColor(realized))
                Text("매도하면 '판 내역'으로 넘어가요. 일부만 팔면 나머지는 보유에 남아요.", fontSize = 12.sp, color = c.faint)
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(sellPrice, qty) }) { Text("매도", color = c.point) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = c.sub) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSoldDialog(record: SoldRecord, onSave: (SoldRecord) -> Unit, onDismiss: () -> Unit) {
    val c = LocalHoldingColors.current
    var sellText by remember { mutableStateOf(record.sellPrice.toString()) }
    var qtyText by remember { mutableStateOf(record.quantity.toString()) }
    var overrideText by remember { mutableStateOf(record.realizedOverride?.toString() ?: "") }
    val sell = sellText.filter { it.isDigit() }.toLongOrNull() ?: record.sellPrice
    val qty = (qtyText.filter { it.isDigit() }.toIntOrNull() ?: record.quantity).coerceAtLeast(1)
    val override = overrideText.trim().toLongOrNull()
    val shownRealized = override ?: ((sell - record.buyPrice) * qty)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.card,
        titleContentColor = c.ink,
        textContentColor = c.sub,
        title = { Text("${record.ticker} 매도 내역 수정", fontWeight = FontWeight.Bold, color = c.ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(sellText, { sellText = it.filter { d -> d.isDigit() }.take(12) }, singleLine = true, label = { Text("매도가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(qtyText, { qtyText = it.filter { d -> d.isDigit() }.take(9) }, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(overrideText, { overrideText = it.filter { d -> d.isDigit() || d == '-' }.take(13) }, singleLine = true, label = { Text("실현손익 직접 입력 (비우면 자동)") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Text("실현손익 ${formatWon(shownRealized)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pnlColor(shownRealized))
                Text(
                    if (override == null) "매도가·수량으로 자동 계산 중" else "직접 입력한 값으로 계산 중",
                    fontSize = 11.sp, color = if (override == null) c.faint else c.point,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(record.copy(sellPrice = sell, quantity = qty, realizedOverride = override)) }) {
                Text("저장", color = c.point)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = c.sub) } },
    )
}

@Composable
private fun DeleteConfirmDialog(label: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalHoldingColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.card,
        titleContentColor = c.ink,
        textContentColor = c.sub,
        title = { Text("삭제", fontWeight = FontWeight.Bold, color = c.ink) },
        text = { Text("'$label'을(를) 삭제할까요? 되돌릴 수 없어요.", fontSize = 14.sp, color = c.sub) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("삭제", color = c.point) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = c.sub) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditHoldingDialog(holding: Holding, viewModel: HoldingViewModel, onSave: (Holding) -> Unit, onSellClick: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalHoldingColors.current
    val candidates by viewModel.stockCandidates.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(holding.ticker) }
    var justPicked by remember { mutableStateOf(true) } // 처음엔 기존 종목명이라 후보 숨김
    var buy by remember { mutableStateOf(holding.buyPrice.toString()) }
    var current by remember { mutableStateOf(holding.currentPrice.toString()) }
    var qty by remember { mutableStateOf(holding.quantity.toString()) }
    var entryDate by remember { mutableStateOf(holding.entryDate) }
    var showDate by remember { mutableStateOf(false) }
    val canSave = name.isNotBlank() && buy.any { it.isDigit() }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.card,
        titleContentColor = c.ink,
        textContentColor = c.sub,
        title = { Text("종목 편집", fontWeight = FontWeight.Bold, color = c.ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(20); justPicked = false; viewModel.searchStock(it) },
                    singleLine = true,
                    label = { Text("종목명 (이름으로 검색)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!justPicked && candidates.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).background(c.line)) {
                        candidates.take(6).forEach { s ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { name = s.name; justPicked = true }.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(s.name, fontSize = 14.sp, color = c.ink)
                                Text("${s.code} · ${s.market}", fontSize = 11.sp, color = c.faint)
                            }
                        }
                    }
                }
                OutlinedTextField(buy, { buy = it.filter { c -> c.isDigit() }.take(12) }, singleLine = true, label = { Text("매수가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(qty, { qty = it.filter { c -> c.isDigit() }.take(9) }, singleLine = true, label = { Text("수량") }, suffix = { Text("주") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(current, { current = it.filter { c -> c.isDigit() }.take(12) }, singleLine = true, label = { Text("현재가") }, suffix = { Text("원") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Text("현재가는 보통 자동으로 갱신돼요. ETF처럼 자동으로 안 잡히는 종목만 여기서 직접 넣으면 돼요.", fontSize = 11.sp, color = c.faint)
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                    Text("편입일 · ${entryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}", color = c.ink)
                }
                OutlinedButton(onClick = onSellClick, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                    Text("이 종목 매도 (판 내역으로)", color = c.point)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val b = buy.filter { it.isDigit() }.toLongOrNull() ?: return@TextButton
                    val cur = current.filter { it.isDigit() }.toLongOrNull() ?: holding.currentPrice
                    val q = qty.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    onSave(holding.copy(ticker = name.trim(), buyPrice = b, currentPrice = cur, quantity = q, entryDate = entryDate))
                },
                enabled = canSave,
            ) { Text("저장", color = c.point) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소", color = c.sub) } },
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
