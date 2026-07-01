package com.babytigerdaddy.shfirstplayground.ui.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.LossBlue
import com.babytigerdaddy.shfirstplayground.ui.screen.trade.ProfitRed
import com.babytigerdaddy.shfirstplayground.ui.theme.LocalHoldingColors
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeShape

/**
 * 설정 화면 — 모양(소프트/샤프) · 색 테마(라이트/다크/파스텔/모노) 선택.
 *
 * 위쪽 미리보기 카드가 지금 고른 조합이 실제로 어떻게 보이는지 즉시 비춘다.
 * 손익 빨강·파랑은 어느 테마든 고정 — 미리보기에도 그대로 드러낸다.
 * 고르는 즉시 전역 반영·저장.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    embedded: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val c = LocalHoldingColors.current

    Column(
        modifier = modifier.fillMaxSize().background(c.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(22.dp))
        if (embedded) {
            // 하단 탭으로 들어온 경우 — 뒤로가기 없이 제목만
            Text("설정", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("←", fontSize = 22.sp, color = c.ink) }
                Text("설정", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
            }
        }

        Spacer(Modifier.height(18.dp))
        AccountSection()

        Spacer(Modifier.height(24.dp))
        PreviewCard()

        Spacer(Modifier.height(24.dp))
        SectionLabel("모양")
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ShapeOption(
                title = "둥근",
                desc = "소프트 · 친근한 곡선",
                sampleRadius = 16.dp,
                selected = settings.shape == ThemeShape.SOFT,
                modifier = Modifier.weight(1f),
            ) { viewModel.setShape(ThemeShape.SOFT) }
            ShapeOption(
                title = "각진",
                desc = "샤프 · 잡지풍 세련",
                sampleRadius = 3.dp,
                selected = settings.shape == ThemeShape.SHARP,
                modifier = Modifier.weight(1f),
            ) { viewModel.setShape(ThemeShape.SHARP) }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "모양은 고른 즉시 적용·저장돼요. 화이트/다크는 폰 설정을 따라 자동으로 바뀌고, 오름 빨강·내림 파랑은 항상 그대로예요.",
            fontSize = 12.sp, color = c.faint,
        )
        Spacer(Modifier.height(28.dp))
    }
}

// ---------- 미리보기 (현재 조합이 실제로 이렇게 보인다) ----------
@Composable
private fun PreviewCard() {
    val c = LocalHoldingColors.current
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(c.card)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("보유노트", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
            Box(Modifier.clip(MaterialTheme.shapes.small).background(c.pointBg).padding(horizontal = 9.dp, vertical = 4.dp)) {
                Text("미리보기", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = c.point)
            }
        }
        MiniRow("삼성전자", "72,000원", "+1.42%", ProfitRed)
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
        MiniRow("카카오", "38,900원", "-2.10%", LossBlue)
    }
}

@Composable
private fun MiniRow(name: String, price: String, rate: String, rateColor: androidx.compose.ui.graphics.Color) {
    val c = LocalHoldingColors.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.ink)
            Text(price, fontSize = 12.sp, color = c.sub)
        }
        Text(rate, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = rateColor)
    }
}

@Composable
private fun SectionLabel(text: String) {
    val c = LocalHoldingColors.current
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.sub)
}

// ---------- 계정 (로그인은 '기록 지키고 싶은 사람'이 켜는 선택 옵션) ----------
@Composable
private fun AccountSection(
    authViewModel: AuthViewModel = hiltViewModel(),
    backupViewModel: BackupViewModel = hiltViewModel(),
) {
    val c = LocalHoldingColors.current
    val context = LocalContext.current
    val user = authViewModel.user
    LaunchedEffect(user) { if (user != null) backupViewModel.refreshCloudState() }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("계정")
        if (user == null) {
            Text(
                "로그인하면 종목·기록이 계정에 백업돼요. 폰을 바꿔도 그대로 살아나요.\n안 해도 이 폰에서 그대로 쓸 수 있어요 — 로그인은 선택이에요.",
                fontSize = 12.sp, color = c.faint,
            )
            GoogleButton(loading = authViewModel.loading) { authViewModel.signInGoogle(context) }
            KakaoButton()
            authViewModel.errorMsg?.let { msg ->
                Text(msg, fontSize = 12.sp, color = ProfitRed)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
                    .background(c.card)
                    .border(BorderStroke(1.dp, c.line), MaterialTheme.shapes.medium)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(user.displayName ?: "로그인됨", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.ink)
                    if (!user.email.isNullOrBlank()) Text(user.email!!, fontSize = 12.sp, color = c.faint)
                }
                OutlinedButton(onClick = { authViewModel.signOut() }) { Text("로그아웃", color = c.sub) }
            }
            BackupSection(backupViewModel)
        }
    }
}

// ---------- 클라우드 백업 (로컬 우선 · 덮어쓰기는 확인 뒤에만) ----------
@Composable
private fun BackupSection(vm: BackupViewModel) {
    val c = LocalHoldingColors.current
    var confirmRestore by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(c.card)
            .border(BorderStroke(1.dp, c.line), MaterialTheme.shapes.medium)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("클라우드 백업", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.ink)
                Text(
                    if (vm.enabled) lastBackupLabel(vm.lastBackupAt) else "켜면 이 폰 기록이 계정에 저장돼요",
                    fontSize = 12.sp, color = c.faint,
                )
            }
            if (vm.syncing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = c.point)
            } else {
                Switch(checked = vm.enabled, onCheckedChange = { vm.toggle(it) })
            }
        }
        if (vm.enabled && !vm.syncing) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { vm.backupNow() }) { Text("지금 백업", color = c.point) }
                if (vm.hasCloudBackup) {
                    OutlinedButton(onClick = { confirmRestore = true }) { Text("클라우드에서 불러오기", color = c.sub) }
                }
            }
        }
        vm.message?.let { Text(it, fontSize = 12.sp, color = c.sub) }
    }
    if (confirmRestore) {
        AlertDialog(
            onDismissRequest = { confirmRestore = false },
            containerColor = Color.White,
            titleContentColor = Color(0xFF16202E),
            textContentColor = Color(0xFF4A5769),
            title = { Text("클라우드에서 불러올까요?", fontWeight = FontWeight.Bold) },
            text = { Text("지금 폰에 있는 종목·판 내역은 클라우드 백업 내용으로 덮어써져요. 되돌릴 수 없어요.") },
            confirmButton = {
                TextButton(onClick = { confirmRestore = false; vm.restoreFromCloud() }) {
                    Text("덮어쓰고 불러오기", color = ProfitRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmRestore = false }) { Text("취소", color = Color(0xFF4A5769)) } },
        )
    }
}

private fun lastBackupLabel(millis: Long): String {
    if (millis <= 0L) return "아직 백업 전이에요"
    val t = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
    return "마지막 백업 · ${t.format(java.time.format.DateTimeFormatter.ofPattern("M.d HH:mm"))}"
}

@Composable
private fun GoogleButton(loading: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .border(BorderStroke(1.dp, Color(0xFFDADCE0)), MaterialTheme.shapes.medium)
            .clickable(enabled = !loading, onClick = onClick)
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF4285F4))
        } else {
            Text("G", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4285F4))
            Spacer(Modifier.size(9.dp))
            Text("구글로 계속하기", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C4043))
        }
    }
}

@Composable
private fun KakaoButton() {
    // 카카오 네이티브 앱키 확보 후 활성화 (지금은 준비 중 표시)
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFFEE500).copy(alpha = 0.45f))
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("카카오로 계속하기 (준비 중)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C1E1E).copy(alpha = 0.6f))
    }
}

// ---------- 모양 옵션 (모서리 미리보기) ----------
@Composable
private fun ShapeOption(
    title: String,
    desc: String,
    sampleRadius: androidx.compose.ui.unit.Dp,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val c = LocalHoldingColors.current
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(c.card)
            .border(BorderStroke(if (selected) 2.dp else 1.dp, if (selected) c.point else c.line), MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        // 모서리 감을 보여주는 샘플 블록
        Box(
            Modifier.fillMaxWidth().height(34.dp)
                .clip(RoundedCornerShape(sampleRadius))
                .background(c.pointBg),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(width = 46.dp, height = 8.dp).clip(RoundedCornerShape(sampleRadius / 2)).background(c.point))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.ink)
            if (selected) Text("✓", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.point)
        }
        Text(desc, fontSize = 11.sp, color = c.faint)
    }
}
