package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood

/** 입력 화면 — 앱 열면 바로 손익 적는 곳. "금액 하나 + 익절/손절 + 기분" 가볍게. */
@Composable
fun InputScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val state by viewModel.input.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    val accent by animateColorAsState(
        targetValue = if (state.isProfit) ProfitRed else LossBlue,
        label = "accent",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column {
            Text(
                text = "오늘 매매",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "그날 실현손익을 일기처럼",
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        // 익절/손절 토글
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            SideToggle(
                text = "익절 +",
                selected = state.isProfit,
                color = ProfitRed,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.onProfitToggle(true) },
            )
            SideToggle(
                text = "손절 −",
                selected = !state.isProfit,
                color = LossBlue,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.onProfitToggle(false) },
            )
        }

        // 금액
        TextField(
            value = state.amountText,
            onValueChange = viewModel::onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                Text(text = "0", fontSize = 34.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            },
            prefix = { Text(text = if (state.isProfit) "+" else "−", fontSize = 28.sp, color = accent) },
            suffix = { Text(text = "원", fontSize = 22.sp) },
            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.End, color = accent),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = accent,
            ),
        )

        // 기분 칩
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "오늘 마음", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TradeMood.entries.forEach { mood ->
                    FilterChip(
                        selected = mood == state.mood,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.onMoodChange(mood)
                        },
                        label = { Text(mood.label(), fontSize = 12.sp) },
                    )
                }
            }
        }

        // 저장
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.save()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = state.canSave,
            colors = ButtonDefaults.buttonColors(containerColor = accent),
        ) {
            Text(text = "오늘 손익 저장", fontSize = 17.sp)
        }
        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.saveRestDay()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "오늘은 쉼 (0원)")
        }

        state.savedDate?.let {
            Card(colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f))) {
                Text(
                    text = "✓ 오늘($it) 저장 완료 — 추이 탭에서 확인",
                    color = accent,
                    modifier = Modifier.padding(14.dp),
                )
            }
        }
    }
}

@Composable
private fun SideToggle(
    text: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(
        targetValue = if (selected) color else MaterialTheme.colorScheme.surfaceVariant,
        label = "toggle-bg",
    )
    val fg = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        onClick = onClick,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, color = fg, fontSize = 16.sp)
        }
    }
}
