package com.babytigerdaddy.shfirstplayground.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
