package com.babytigerdaddy.shfirstplayground.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 3 step onboarding: (1) 가치 + evidence, (2) 사용법, (3) 예시 질문 + CTA.
 *
 * 마지막 step에서 CTA 누르면 onFinish 호출 → 다시 안 보임 (Navigation popUpTo inclusive).
 */
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        StepIndicator(currentStep = step, total = totalSteps)
        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (step) {
                0 -> ValueSlide()
                1 -> HowToSlide()
                2 -> ExampleSlide()
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (step > 0) {
                OutlinedButton(onClick = { step -= 1 }) { Text("이전") }
            } else {
                Spacer(modifier = Modifier.size(1.dp))
            }
            Button(
                onClick = {
                    if (step < totalSteps - 1) step += 1 else onFinish()
                },
            ) {
                Text(if (step < totalSteps - 1) "다음" else "시작하기")
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(total) { index ->
            val active = index == currentStep
            Surface(
                modifier = Modifier.padding(horizontal = 4.dp).size(if (active) 12.dp else 8.dp),
                shape = CircleShape,
                color = if (active) MaterialTheme.colorScheme.primary else Color.LightGray,
                content = {},
            )
        }
    }
}

@Composable
private fun ValueSlide() {
    Text(text = "👋", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "아이와 본 영상,\n그 다음 한 줄이 발달을 바꿔요",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "발달 연구상 부모와의 짧은 대화(joint media engagement)가 콘텐츠 quality보다 5살 정서·인지 발달에 더 큰 변수예요.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun HowToSlide() {
    Text(text = "🎬", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "사용법은 간단해요",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("1. 추천 에피소드 골라요", style = MaterialTheme.typography.bodyLarge)
        Text("2. 아이와 함께 영상 봐요", style = MaterialTheme.typography.bodyLarge)
        Text("3. 시청 후 질문 카드 한 장만 던져 봐요", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ExampleSlide() {
    Text(text = "💬", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "이런 질문이 나와요",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = "\"블루이랑 빙고가 어느 순간 '이제 그만하는 게 좋겠다' 싶었을까?\"",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "이번 에피소드 보고 직접 던져 보세요.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
