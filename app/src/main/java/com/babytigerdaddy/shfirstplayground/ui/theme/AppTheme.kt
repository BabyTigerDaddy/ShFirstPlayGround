package com.babytigerdaddy.shfirstplayground.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 보유노트 테마 뼈대 (2026-07-01, 아빠 지시 디자인 개편).
 *
 * 두 축으로 조합:
 *  - [ThemeShape] 모양 골격 : SOFT(둥근·친근) / SHARP(각진·잡지풍 세련)
 *  - [ThemePalette] 색 테마 : LIGHT / DARK / PASTEL / MONO
 *
 * 손익 빨강/파랑([ProfitRed]/[LossBlue])은 테마에서 제외 — 어느 테마든 고정.
 * (한국 증시 관습: 오름 빨강 / 내림 파랑. 팔레트가 이걸 흐리면 안 됨.)
 *
 * 분담: 이 파일의 구조·LIGHT/SOFT 기준값은 보고(뼈대).
 *       DARK/PASTEL/MONO 색값·SHARP 튜닝·화면 색 배선은 문서(룩).
 *       아래 팔레트 상수의 실제 색은 문서가 감각으로 교체 예정(현재는 동작용 기본값).
 */

enum class ThemeShape { SOFT, SHARP }

enum class ThemePalette { LIGHT, DARK, PASTEL, MONO }

/** 화면이 참조하는 의미색 묶음. 팔레트마다 한 세트. (손익색은 여기 없음 = 고정 상수) */
data class HoldingColors(
    val bg: Color,       // 화면 배경
    val card: Color,     // 카드·표 배경
    val ink: Color,      // 본문 텍스트
    val sub: Color,      // 보조 텍스트
    val faint: Color,    // 흐린 텍스트·힌트
    val line: Color,     // 구분선·테두리
    val point: Color,    // 강조 포인트(선택·링크)
    val pointBg: Color,  // 포인트 옅은 배경(배지 등)
)

// LIGHT = 현재 보유노트 화면 색(기준). 뼈대가 채워 바로 동작.
val LightHoldingColors = HoldingColors(
    bg = Color(0xFFEDEFF3),
    card = Color(0xFFFFFFFF),
    ink = Color(0xFF0E1216),
    sub = Color(0xFF4B5562),
    faint = Color(0xFF818B99),
    line = Color(0xFFE2E6EC),
    point = Color(0xFF5A49C8),
    pointBg = Color(0xFFEAE7FB),
)

// DARK — 딥네이비 차콜에 민트 형광 포인트(아이콘 A 민트와 통일). MZ 다크 감성.
val DarkHoldingColors = HoldingColors(
    bg = Color(0xFF0B0E13),
    card = Color(0xFF161B23),
    ink = Color(0xFFEAEEF4),
    sub = Color(0xFFA3ADB9),
    faint = Color(0xFF69727E),
    line = Color(0xFF232A34),
    point = Color(0xFF3DD6C0),   // 민트 형광 — 브랜드 포인트
    pointBg = Color(0xFF10322C),
)

// PASTEL — 화사한 라벤더+화이트. 말랑하고 밝은 톤.
val PastelHoldingColors = HoldingColors(
    bg = Color(0xFFFBF6FF),
    card = Color(0xFFFFFFFF),
    ink = Color(0xFF423A57),
    sub = Color(0xFF746A8C),
    faint = Color(0xFFAAA1C2),
    line = Color(0xFFF0E9FB),
    point = Color(0xFFA97DF0),
    pointBg = Color(0xFFF2EAFE),
)

// MONO — 또렷한 흑백. 포인트도 잉크색 하나로 통일한 극미니멀 세련.
val MonoHoldingColors = HoldingColors(
    bg = Color(0xFFF4F4F4),
    card = Color(0xFFFFFFFF),
    ink = Color(0xFF0E0E0E),
    sub = Color(0xFF565656),
    faint = Color(0xFF9A9A9A),
    line = Color(0xFFE6E6E6),
    point = Color(0xFF0E0E0E),
    pointBg = Color(0xFFEAEAEA),
)

fun holdingColorsFor(palette: ThemePalette): HoldingColors = when (palette) {
    ThemePalette.LIGHT -> LightHoldingColors
    ThemePalette.DARK -> DarkHoldingColors
    ThemePalette.PASTEL -> PastelHoldingColors
    ThemePalette.MONO -> MonoHoldingColors
}

/** 화면 어디서든 현재 팔레트 색을 꺼내 쓰는 통로. 기본은 LIGHT. */
val LocalHoldingColors = staticCompositionLocalOf { LightHoldingColors }

// 모양 골격 — SOFT는 기존 보유노트 둥근 톤에 맞춘 값, SHARP는 각지게.
val SoftShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

val SharpShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(3.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(6.dp),
    extraLarge = RoundedCornerShape(8.dp),
)

fun shapesFor(shape: ThemeShape): Shapes = when (shape) {
    ThemeShape.SOFT -> SoftShapes
    ThemeShape.SHARP -> SharpShapes
}
