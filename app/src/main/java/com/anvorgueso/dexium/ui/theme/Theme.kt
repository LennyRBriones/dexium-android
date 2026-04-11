package com.anvorgueso.dexium.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DexiumColorScheme = darkColorScheme(
    primary = GlassBlue,
    onPrimary = Color.White,
    primaryContainer = GlassBlueDark,
    onPrimaryContainer = GlassWhite,
    secondary = GlassBlueSoft,
    onSecondary = DeepNavy,
    secondaryContainer = MidNavy,
    onSecondaryContainer = GlassWhite,
    tertiary = GlassBlueSoft,
    onTertiary = DeepNavy,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = DarkNavy,
    onSurface = TextPrimary,
    surfaceVariant = MidNavy,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = GlassBorder
)

data class GlassColors(
    val surface: Color = GlassSurface,
    val surfaceLight: Color = GlassSurfaceLight,
    val border: Color = GlassBorder,
    val borderLight: Color = GlassBorderLight,
    val glow: Color = GlowBlue,
    val glowSoft: Color = GlowBlueSoft,
    val accent: Color = GlassBlue,
    val accentSoft: Color = GlassBlueSoft,
    val backgroundStart: Color = DeepNavy,
    val backgroundEnd: Color = DarkNavy
)

val LocalGlassColors = staticCompositionLocalOf { GlassColors() }

object DexiumGlass {
    val colors: GlassColors
        @Composable get() = LocalGlassColors.current
}

@Composable
fun DexiumTheme(
    content: @Composable () -> Unit
) {
    val glassColors = GlassColors()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(
        LocalGlassColors provides glassColors
    ) {
        MaterialTheme(
            colorScheme = DexiumColorScheme,
            typography = DexiumTypography,
            content = content
        )
    }
}
