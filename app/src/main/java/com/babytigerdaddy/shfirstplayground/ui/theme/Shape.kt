package com.babytigerdaddy.shfirstplayground.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * v4 shape 가이드. 엄마 사용 톤 — 카드·버튼 corner 큼직하게.
 *
 * v3 Material default 대비 small 8→12dp, medium 12→20dp, large 16→28dp.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)
