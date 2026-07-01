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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemePalette
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeShape
import com.babytigerdaddy.shfirstplayground.ui.theme.holdingColorsFor

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

        Spacer(Modifier.height(24.dp))
        SectionLabel("색 테마")
        Spacer(Modifier.height(10.dp))
        PaletteOption("라이트", "화이트 기본", ThemePalette.LIGHT, settings.palette == ThemePalette.LIGHT) { viewModel.setPalette(ThemePalette.LIGHT) }
        Spacer(Modifier.height(9.dp))
        PaletteOption("다크", "검정 배경 · 민트 형광", ThemePalette.DARK, settings.palette == ThemePalette.DARK) { viewModel.setPalette(ThemePalette.DARK) }
        Spacer(Modifier.height(9.dp))
        PaletteOption("파스텔", "연보라 말랑", ThemePalette.PASTEL, settings.palette == ThemePalette.PASTEL) { viewModel.setPalette(ThemePalette.PASTEL) }
        Spacer(Modifier.height(9.dp))
        PaletteOption("모노", "흑백 미니멀", ThemePalette.MONO, settings.palette == ThemePalette.MONO) { viewModel.setPalette(ThemePalette.MONO) }

        Spacer(Modifier.height(20.dp))
        Text(
            "고른 즉시 앱 전체에 적용되고 저장돼요. 오름 빨강·내림 파랑은 어느 테마든 그대로예요.",
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
private fun AccountSection(authViewModel: AuthViewModel = hiltViewModel()) {
    val c = LocalHoldingColors.current
    val context = LocalContext.current
    val user = authViewModel.user
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
                    Text("기록이 이 계정에 백업돼요", fontSize = 11.sp, color = c.point)
                }
                OutlinedButton(onClick = { authViewModel.signOut() }) { Text("로그아웃", color = c.sub) }
            }
        }
    }
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

// ---------- 색 테마 옵션 (팔레트 스와치) ----------
@Composable
private fun PaletteOption(
    title: String,
    desc: String,
    palette: ThemePalette,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val c = LocalHoldingColors.current
    val p = holdingColorsFor(palette)
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(c.card)
            .border(BorderStroke(if (selected) 2.dp else 1.dp, if (selected) c.point else c.line), MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 스와치 — 배경·카드·포인트 세 색을 겹친 미리보기
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(11.dp)).background(p.bg)
                .border(1.dp, c.line, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(26.dp).clip(RoundedCornerShape(7.dp)).background(p.card)) {
                Box(Modifier.padding(6.dp).size(10.dp).clip(RoundedCornerShape(5.dp)).background(p.point))
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.ink)
            Text(desc, fontSize = 12.sp, color = c.faint)
        }
        if (selected) Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.point)
    }
}
