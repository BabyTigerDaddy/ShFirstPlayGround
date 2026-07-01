package com.babytigerdaddy.shfirstplayground.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.babytigerdaddy.shfirstplayground.ui.theme.LocalHoldingColors
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemePalette
import com.babytigerdaddy.shfirstplayground.ui.theme.ThemeShape

/**
 * 설정 화면 골격 — 모양(소프트/샤프) · 색 테마(라이트/다크/파스텔/모노) 선택.
 *
 * 고르는 즉시 [SettingsViewModel]로 저장·전역 반영. 골격만 보고가 깔고,
 * 미리보기·스와치·다듬은 룩은 문서가 얹는다.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val c = LocalHoldingColors.current

    Column(modifier = Modifier.fillMaxSize().background(c.bg).padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("←", fontSize = 22.sp, color = c.ink) }
            Text("설정", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = c.ink)
        }

        Spacer(Modifier.height(16.dp))
        Text("모양", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.sub)
        Spacer(Modifier.height(8.dp))
        OptionRow("둥근 (소프트)", settings.shape == ThemeShape.SOFT) { viewModel.setShape(ThemeShape.SOFT) }
        OptionRow("각진 (샤프)", settings.shape == ThemeShape.SHARP) { viewModel.setShape(ThemeShape.SHARP) }

        Spacer(Modifier.height(20.dp))
        Text("색 테마", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.sub)
        Spacer(Modifier.height(8.dp))
        OptionRow("라이트", settings.palette == ThemePalette.LIGHT) { viewModel.setPalette(ThemePalette.LIGHT) }
        OptionRow("다크", settings.palette == ThemePalette.DARK) { viewModel.setPalette(ThemePalette.DARK) }
        OptionRow("파스텔", settings.palette == ThemePalette.PASTEL) { viewModel.setPalette(ThemePalette.PASTEL) }
        OptionRow("모노", settings.palette == ThemePalette.MONO) { viewModel.setPalette(ThemePalette.MONO) }

        Spacer(Modifier.height(24.dp))
        Text(
            "고른 즉시 앱 전체에 적용돼요. 손익 빨강·파랑은 어느 테마든 그대로예요.",
            fontSize = 12.sp, color = c.faint,
        )
    }
}

@Composable
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    val c = LocalHoldingColors.current
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(if (selected) c.pointBg else c.card)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = c.ink)
        if (selected) Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.point)
    }
    Spacer(Modifier.height(8.dp))
}
