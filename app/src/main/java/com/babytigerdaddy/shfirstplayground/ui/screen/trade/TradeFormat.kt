package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.ui.graphics.Color
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood

/** 익절(+) 빨강 / 손절(−) 파랑 / 0원 회색 — 한국 증시 관습. */
val ProfitRed = Color(0xFFE53935)
val LossBlue = Color(0xFF1E88E5)
val FlatGray = Color(0xFF9E9E9E)

/** 부호에 따른 색. 0은 회색. */
fun pnlColor(value: Long): Color = when {
    value > 0 -> ProfitRed
    value < 0 -> LossBlue
    else -> FlatGray
}

/** 1234567 → "+1,234,567원" / 음수는 "-..." / 0은 "0원". */
fun formatWon(value: Long): String {
    val sign = if (value > 0) "+" else if (value < 0) "-" else ""
    val abs = kotlin.math.abs(value)
    val grouped = abs.toString().reversed().chunked(3).joinToString(",").reversed()
    return "$sign${grouped}원"
}

fun TradeMood.label(): String = when (this) {
    TradeMood.DISCIPLINED -> "원칙대로"
    TradeMood.SATISFIED -> "만족"
    TradeMood.FLAT -> "무덤덤"
    TradeMood.REGRET -> "아쉬움"
    TradeMood.OVERTRADED -> "과매매"
}
