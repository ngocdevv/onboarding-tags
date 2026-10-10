package com.ngocdevv.onboardingtags.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Port of `src/constants/theme.ts`. */
data class Palette(
    val text: Color,
    val background: Color,
    val backgroundElement: Color,
    val backgroundSelected: Color,
    val textSecondary: Color,
)

val LightPalette = Palette(
    text = Color(0xFF000000),
    background = Color(0xFFFFFFFF),
    backgroundElement = Color(0xFFF0F0F3),
    backgroundSelected = Color(0xFFE0E1E6),
    textSecondary = Color(0xFF60646C),
)

val DarkPalette = Palette(
    text = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    backgroundElement = Color(0xFF212225),
    backgroundSelected = Color(0xFF2E3135),
    textSecondary = Color(0xFFB0B4BA),
)

@Composable
fun palette(): Palette = if (isSystemInDarkTheme()) DarkPalette else LightPalette

object Spacing {
    val half = 2.dp
    val one = 4.dp
    val two = 8.dp
    val three = 16.dp
    val four = 24.dp
    val five = 32.dp
    val six = 64.dp
}

val BottomTabInset = 80.dp
val MaxContentWidth = 800.dp

@Composable
fun OnboardingTagsTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme, content = content)
}
