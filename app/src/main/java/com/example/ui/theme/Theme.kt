package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val TvDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    secondary = PhosphorGreen,
    onSecondary = Color.Black,
    tertiary = SynthMagenta,
    onTertiary = Color.White,
    background = DarkTvBg,
    onBackground = TextWhite,
    surface = DarkTvSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkTvSurfaceVariant,
    onSurfaceVariant = TextMuted
)

private val TvLightColorScheme = lightColorScheme(
    primary = Color(0xFF0091EA),
    secondary = Color(0xFF00C853),
    tertiary = Color(0xFFAA00FF),
    background = Color(0xFF10121D),
    surface = Color(0xFF181B2B),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep custom TV dark neon styling consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TvDarkColorScheme else TvDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
