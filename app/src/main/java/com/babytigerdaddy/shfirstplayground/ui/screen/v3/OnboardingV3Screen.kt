package com.babytigerdaddy.shfirstplayground.ui.screen.v3

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babytigerdaddy.shfirstplayground.data.local.AppStateStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingV3ViewModel @Inject constructor(
    private val appStateStore: AppStateStore,
) : ViewModel() {
    fun markCompleted(onDone: () -> Unit) {
        viewModelScope.launch {
            appStateStore.markOnboardingCompleted()
            onDone()
        }
    }
}

@Composable
fun OnboardingV3Screen(
    onFinish: () -> Unit,
    viewModel: OnboardingV3ViewModel = hiltViewModel(),
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 2

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
                1 -> StartSlide()
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
            Button(onClick = {
                if (step < totalSteps - 1) {
                    step += 1
                } else {
                    viewModel.markCompleted(onFinish)
                }
            }) {
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
    Text(text = "🌅", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "5살 부모의\n작은 routine 도우미",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "아침·저녁 짧은 체크리스트와 매일 한 줄 발달 카드. 부담 없이 누적되면 6개월 후 가장 큰 차이가 나요.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun StartSlide() {
    Text(text = "✨", fontSize = 64.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "지금부터 시작해 봐요",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "오늘 routine 한 칸만 체크하셔도 충분해요. 매일 조금씩이 쌓여요.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
