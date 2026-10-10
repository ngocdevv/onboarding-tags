package com.ngocdevv.onboardingtags.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex

private const val DURATION_MS = 600

/**
 * Port of `src/components/animated-icon.tsx` (`AnimatedSplashOverlay`).
 *
 * Keyframes from the original (600 ms total):
 *   0%   scale = screenHeight / 90, opacity 1
 *   20%  opacity 1
 *   70%  opacity 0 (elastic easing)
 *   100% scale = 1, opacity 0
 */
@Composable
fun AnimatedSplashOverlay() {
    var visible by remember { mutableStateOf(true) }
    if (!visible) return

    val initialScaleFactor = LocalConfiguration.current.screenHeightDp / 90f
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(DURATION_MS, easing = LinearEasing))
        visible = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(1000f)
            .graphicsLayer {
                val p = progress.value
                val eased = 1f - (1f - p) * (1f - p) // ease-out on scale
                val s = initialScaleFactor + (1f - initialScaleFactor) * eased
                scaleX = s
                scaleY = s
                alpha = when {
                    p <= 0.2f -> 1f
                    p >= 0.7f -> 0f
                    else -> {
                        val t = (p - 0.2f) / 0.5f
                        1f - t * (2f - t) // ease-out fade
                    }
                }
            }
            .background(Color(0xFF208AEF))
    )
}
