package com.ngocdevv.onboardingtags.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Compose stand-in for react-native-reanimated's `entering` layout animations
 * (`FadeInDown`, `FadeInUp`, `ZoomIn`) used across the onboarding screens.
 */
enum class Entering { FadeInDown, FadeInUp, ZoomIn }

/** Mirrors `entering={FadeInDown.delay(ms).duration(ms).springify()}`. */
@Composable
fun Modifier.entering(
    type: Entering,
    delayMillis: Int = 0,
    durationMillis: Int = 800,
    springify: Boolean = true,
): Modifier {
    val progress = remember { Animatable(0f) }
    val offsetPx = with(LocalDensity.current) { 25.dp.toPx() }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        if (springify) {
            progress.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 110f))
        } else {
            progress.animateTo(1f, tween(durationMillis, easing = FastOutSlowInEasing))
        }
    }

    return graphicsLayer {
        val p = progress.value
        // Reanimated's ZoomIn only animates scale, not opacity.
        alpha = if (type == Entering.ZoomIn) 1f else p.coerceIn(0f, 1f)
        when (type) {
            Entering.FadeInDown -> translationY = -offsetPx * (1f - p)
            Entering.FadeInUp -> translationY = offsetPx * (1f - p)
            Entering.ZoomIn -> {
                scaleX = p
                scaleY = p
            }
        }
    }
}
