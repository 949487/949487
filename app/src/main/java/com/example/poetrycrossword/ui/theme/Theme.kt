package com.example.poetrycrossword.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF7A4DFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF006A6A),
    tertiary = androidx.compose.ui.graphics.Color(0xFFB3261E),
    background = androidx.compose.ui.graphics.Color(0xFFFDF8FF),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFD2BCFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF4FD8D7),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    background = androidx.compose.ui.graphics.Color(0xFF15121C),
    surface = androidx.compose.ui.graphics.Color(0xFF15121C)
)

@Composable
fun PoetryCrosswordTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = ExpressiveTypography,
        shapes = ExpressiveShapes,
        content = content
    )
}
