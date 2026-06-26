package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.ui.graphics.Color
import com.babytigerdaddy.shfirstplayground.domain.model.TradeMood

/** 익절(+) 빨강 / 손절(−) 파랑 / 0원 회색 — 한국 증시 관습. */
val ProfitRed = Color(0xFFE53935)
val LossBlue = Color(0xFF1E88E5)
val FlatGray = Color(0xFF9E9E9E)

// 누적선 0선 위/아래 면 (반투명)
val ProfitFill = Color(0x2EE53935)
val LossFill = Color(0x2E1E88E5)

// 매매일지 중립 톤 (v4 coral 테마와 분리 — 화면에서 직접 지정)
val TradeBg = Color(0xFFF4F6F8)
val TradeCard = Color(0xFFFFFFFF)
val TradeInk = Color(0xFF14181D)
val TradeMuted = Color(0xFF6B7480)
val TradeLine = Color(0xFFE4E8ED)

/** 기분 5종 색 — 칩 강조용. */
fun TradeMood.tint(): Color = when (this) {
    TradeMood.DISCIPLINED -> Color(0xFF16A394)
    TradeMood.SATISFIED -> Color(0xFF4CAF50)
    TradeMood.FLAT -> Color(0xFF9AA3AD)
    TradeMood.REGRET -> Color(0xFFF0A030)
    TradeMood.OVERTRADED -> Color(0xFFE5392E)
}

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
