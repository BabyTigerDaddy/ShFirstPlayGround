package com.babytigerdaddy.shfirstplayground.ui.screen.trade

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.babytigerdaddy.shfirstplayground.domain.model.DailyPnlPoint

/**
 * 누적 손익선 — 외부 라이브러리 없이 Compose Canvas로 직접 그린다.
 * 왼쪽부터 스르륵 그려지고, 0선 위 구간은 빨강 면(누적 +)·아래는 파랑 면(누적 −).
 * 면 색 기준 = '누적값'의 0선 위/아래 (형과 맞춘 그대로).
 */
@Composable
fun CumulativeLineChart(daily: List<DailyPnlPoint>, modifier: Modifier = Modifier) {
    val progress = remember(daily.size) { Animatable(0f) }
    LaunchedEffect(daily.size) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 1100))
    }

    Canvas(modifier = modifier) {
        if (daily.size < 2) return@Canvas

        val values = daily.map { it.cumulative.toFloat() }
        val maxV = maxOf(values.maxOrNull() ?: 0f, 0f)
        val minV = minOf(values.minOrNull() ?: 0f, 0f)
        val range = (maxV - minV).takeIf { it > 0f } ?: 1f

        val w = size.width
        val h = size.height
        val padV = 16f
        val plotH = h - padV * 2
        fun yFor(v: Float) = padV + plotH * (maxV - v) / range
        fun xFor(i: Int) = w * i / (daily.size - 1)
        val zeroY = yFor(0f)

        val line = Path().apply {
            daily.forEachIndexed { i, p ->
                val x = xFor(i)
                val y = yFor(p.cumulative.toFloat())
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        val fill = Path().apply {
            addPath(line)
            lineTo(xFor(daily.size - 1), zeroY)
            lineTo(xFor(0), zeroY)
            close()
        }

        // 왼쪽부터 드러나는 애니메이션
        clipRect(right = w * progress.value) {
            clipRect(top = 0f, bottom = zeroY) { drawPath(fill, ProfitFill) }
            clipRect(top = zeroY, bottom = h) { drawPath(fill, LossFill) }
            drawPath(
                path = line,
                color = TradeInk,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }

        // 0선 (점선)
        drawLine(
            color = TradeLine,
            start = Offset(0f, zeroY),
            end = Offset(w, zeroY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
        )

        // 마지막 점 마커
        val lastY = yFor(daily.last().cumulative.toFloat())
        val markColor = if (daily.last().cumulative >= 0L) ProfitRed else LossBlue
        drawCircle(
            color = markColor,
            radius = 5.dp.toPx() * progress.value,
            center = Offset(w, lastY),
        )
    }
}
