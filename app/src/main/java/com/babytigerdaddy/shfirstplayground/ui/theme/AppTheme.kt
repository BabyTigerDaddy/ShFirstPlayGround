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

// 아래 3종은 동작용 placeholder — 문서가 룩 얹을 때 실제 감성값으로 교체.
val DarkHoldingColors = HoldingColors(
    bg = Color(0xFF0E1116),
    card = Color(0xFF181D25),
    ink = Color(0xFFEDF1F6),
    sub = Color(0xFFAAB4C0),
    faint = Color(0xFF6C7683),
    line = Color(0xFF262D37),
    point = Color(0xFF39E6C3),   // 형광 포인트(MZ 다크)
    pointBg = Color(0xFF14322D),
)

val PastelHoldingColors = HoldingColors(
    bg = Color(0xFFFBF7FF),
    card = Color(0xFFFFFFFF),
    ink = Color(0xFF3A3550),
    sub = Color(0xFF6E6788),
    faint = Color(0xFFA79FC0),
    line = Color(0xFFEFE8FA),
    point = Color(0xFFB18CF0),
    pointBg = Color(0xFFF1E9FE),
)

val MonoHoldingColors = HoldingColors(
    bg = Color(0xFFF6F6F6),
    card = Color(0xFFFFFFFF),
    ink = Color(0xFF111111),
    sub = Color(0xFF555555),
    faint = Color(0xFF999999),
    line = Color(0xFFE4E4E4),
    point = Color(0xFF111111),
    pointBg = Color(0xFFEDEDED),
)

fun holdingColorsFor(palette: ThemePalette): HoldingColors = when (palette) {
    ThemePalette.LIGHT -> LightHoldingColors
    ThemePalette.DARK -> DarkHoldingColors
    ThemePalette.PASTEL -> PastelHoldingColors
    ThemePalette.MONO -> MonoHoldingColors
}

/** 화면 어디서든 현재 팔레트 색을 꺼내 쓰는 통로. 기본은 LIGHT. */
val LocalHoldingColors = staticCompositionLocalOf { LightHoldingColors }

// 모양 골격 — SOFT는 현재 둥근 값, SHARP는 각지게.
val SoftShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
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
