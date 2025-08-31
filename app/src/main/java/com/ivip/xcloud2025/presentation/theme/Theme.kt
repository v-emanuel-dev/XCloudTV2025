package com.ivip.xcloudtv2025.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

/**
 * Esquema de cores escuro, otimizado para Android TV.
 */
private val DarkColorScheme = darkColorScheme(
    // Cores primárias
    primary = XcloudBlue,
    onPrimary = Color.White,
    primaryContainer = XcloudBlueDark,
    onPrimaryContainer = Color.White,

    // Cores secundárias
    secondary = XcloudCyan,
    onSecondary = Color.Black,
    secondaryContainer = XcloudCyanDark,
    onSecondaryContainer = Color.White,

    // Cores terciárias
    tertiary = XcloudOrange,
    onTertiary = Color.Black,
    tertiaryContainer = XcloudOrangeDark,
    onTertiaryContainer = Color.White,

    // Cores de fundo
    background = XcloudDarkGray,
    onBackground = Color.White,

    // Cores de superfície
    surface = XcloudSurfaceGray,
    onSurface = Color.White,
    surfaceVariant = XcloudMediumGray,
    onSurfaceVariant = Color(0xFFE0E0E0),

    // Cores de container de superfície
    surfaceContainer = XcloudMediumGray,
    surfaceContainerHigh = XcloudLightGray,
    surfaceContainerHighest = Color(0xFF3C3C3C),

    // Cores de erro
    error = XcloudError,
    onError = Color.White,
    errorContainer = Color(0xFF5D1A1A),
    onErrorContainer = Color(0xFFFFDADA),

    // Cores de contorno
    outline = Color(0xFF5C5C5C),
    outlineVariant = Color(0xFF3A3A3A),

    // Cores de superfície inversa
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313030),
    inversePrimary = XcloudBlue,

    // Cores do scrim
    scrim = Color.Black
)

/**
 * Tema principal do aplicativo XcloudTV, configurado para usar sempre o tema escuro.
 */
@Composable
fun XcloudTVTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = XcloudTypography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}