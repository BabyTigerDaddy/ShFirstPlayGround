package com.babytigerdaddy.shfirstplayground.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Coral,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Warm,
    onPrimaryContainer = SoftRose,
    secondary = Peach,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Warm,
    onSecondaryContainer = SoftRose,
    tertiary = Pink,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = Cream,
    onBackground = SoftRose,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = SoftRose,
    surfaceVariant = Warm,
    onSurfaceVariant = SoftRose,
)

private val DarkColors = darkColorScheme(
    primary = DeepCoral,
    secondary = Peach,
    tertiary = Pink,
)

@Composable
fun ShFirstPlayGroundTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // v4는 brand 톤 일관 위해 default off
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}

/**
 * 보유노트 — 사용자가 고른 [ThemeSettings](모양·색)로 테마 적용.
 *
 * 모양은 [shapesFor], 색은 [holdingColorsFor]로 골라 [LocalHoldingColors]에 실어
 * 화면 전체에 흘려보낸다. 손익 빨강/파랑은 여기 관여 안 함(고정 상수).
 */
@Composable
fun ShFirstPlayGroundTheme(
    settings: ThemeSettings,
    content: @Composable () -> Unit,
) {
    val holdingColors = holdingColorsFor(settings.palette)
    val colorScheme = if (settings.palette == ThemePalette.DARK) DarkColors else LightColors
    CompositionLocalProvider(LocalHoldingColors provides holdingColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = shapesFor(settings.shape),
            content = content,
        )
    }
}
