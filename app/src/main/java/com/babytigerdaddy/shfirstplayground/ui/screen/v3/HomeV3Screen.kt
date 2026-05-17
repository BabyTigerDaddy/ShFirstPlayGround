package com.babytigerdaddy.shfirstplayground.ui.screen.v3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Path B v3 home scaffold. Phase 1 = routine 체크리스트 + 마이크로카드 1장 + 메모 입력.
 *
 * 이번 commit은 stub data로 흐름 깔기. ViewModel + Repository 연동은 형 다음 commit 받은 뒤.
 */
@Composable
fun HomeV3Screen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Header()
        MicroCardSection()
        RoutineSection(title = "아침 routine", steps = MORNING_STUB)
        RoutineSection(title = "저녁 routine", steps = EVENING_STUB)
        MemoSection()
    }
}

@Composable
private fun Header() {
    Column {
        Text(text = "오늘", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "은하와 함께", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun MicroCardSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "오늘의 카드", style = MaterialTheme.typography.labelMedium)
            Text(
                text = "5살 정서 발달은 '기분의 이름 붙이기'에서 시작해요",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "오늘 누나가 답답하거나 화나는 순간이 있었다면 \"지금 어떤 마음이야?\" 한 줄 물어봐 주세요. 부모가 이름을 함께 붙여주는 경험이 자기조절의 첫 단계예요.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun RoutineSection(title: String, steps: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                steps.forEach { step ->
                    var checked by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checked, onCheckedChange = { checked = it })
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = step, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoSection() {
    var highlight by remember { mutableStateOf("") }
    var challenge by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "오늘의 한 줄 메모", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = highlight,
            onValueChange = { highlight = it },
            label = { Text("잘한 점") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = challenge,
            onValueChange = { challenge = it },
            label = { Text("도전한 점") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
    }
}

private val MORNING_STUB = listOf("일어나기", "세수하기", "아침 식사", "등원 준비")
private val EVENING_STUB = listOf("저녁 식사", "씻기", "이야기 한 권", "잠자기")
