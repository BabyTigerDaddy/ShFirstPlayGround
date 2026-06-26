package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood

/** 입력 화면 — 앱 열면 바로 손익 적는 곳. "금액 하나 + 익절/손절 + 기분" 가볍게, 저장은 3초. */
@Composable
fun InputScreen(viewModel: TradeViewModel = hiltViewModel()) {
    val state by viewModel.input.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    val accent by animateColorAsState(
        targetValue = if (state.isProfit) ProfitRed else LossBlue,
        label = "accent",
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column {
            Text(text = "오늘 매매", fontSize = 13.sp, color = TradeMuted)
            Text(
                text = "그날 실현손익을 일기처럼",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TradeInk,
            )
        }

        // 익절/손절 토글
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            SideToggle("익절 +", state.isProfit, ProfitRed, Modifier.weight(1f)) {
                viewModel.onProfitToggle(true)
            }
            SideToggle("손절 −", !state.isProfit, LossBlue, Modifier.weight(1f)) {
                viewModel.onProfitToggle(false)
            }
        }

        // 금액
        TextField(
            value = state.amountText,
            onValueChange = viewModel::onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                Text(text = "0", fontSize = 34.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), color = TradeMuted)
            },
            prefix = { Text(text = if (state.isProfit) "+" else "−", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = accent) },
            suffix = { Text(text = "원", fontSize = 22.sp, color = TradeMuted) },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = accent,
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TradeCard,
                unfocusedContainerColor = TradeCard,
                focusedIndicatorColor = accent,
                unfocusedIndicatorColor = TradeLine,
            ),
        )

        // 기분 칩
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "오늘 마음", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TradeInk)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TradeMood.entries.forEach { mood ->
                    val tint = mood.tint()
                    FilterChip(
                        selected = mood == state.mood,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.onMoodChange(mood)
                        },
                        label = { Text(mood.label(), fontSize = 12.sp) },
                        leadingIcon = {
                            Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = tint, content = {})
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tint.copy(alpha = 0.16f),
                            selectedLabelColor = tint,
                            selectedLeadingIconColor = tint,
                        ),
                    )
                }
            }
        }

        // 저장 — press 시 살짝 눌리는 모션
        val pressScale by animateFloatAsState(
            targetValue = if (state.saving) 0.97f else 1f,
            animationSpec = tween(140),
            label = "press",
        )
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.save()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).scale(pressScale),
            enabled = state.canSave,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accent),
        ) {
            Text(text = "오늘 손익 저장", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.saveRestDay()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(text = "오늘은 쉼 (0원)", color = TradeMuted)
        }

        AnimatedVisibility(
            visible = state.savedDate != null,
            enter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.9f),
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f)),
            ) {
                Text(
                    text = "✓ 오늘 저장 완료 — 추이 탭에서 확인",
                    color = accent,
                    fontWeight = FontWeight.Bold,
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
        targetValue = if (selected) color else TradeCard,
        label = "toggle-bg",
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.97f,
        animationSpec = tween(160),
        label = "toggle-scale",
    )
    val fg = if (selected) Color.White else TradeMuted
    Card(
        modifier = modifier.height(52.dp).scale(scale),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        onClick = onClick,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, color = fg, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
