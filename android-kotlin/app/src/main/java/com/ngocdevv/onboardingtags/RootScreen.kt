package com.ngocdevv.onboardingtags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ngocdevv.onboardingtags.screens.AppTabs
import com.ngocdevv.onboardingtags.screens.TagsScreen
import com.ngocdevv.onboardingtags.screens.WelcomeScreen
import com.ngocdevv.onboardingtags.ui.components.AnimatedSplashOverlay

/**
 * Port of `src/app/_layout.tsx`.
 * Drives the onboarding flow: 0 = Welcome, 1 = Tags, 2 = Tabs.
 */
@Composable
fun RootScreen() {
    var onboardingStep by rememberSaveable { mutableIntStateOf(0) }

    val handleWelcomeContinue = { onboardingStep = 1 }

    // Kept for parity with the original `handleTagsContinue`.
    @Suppress("UNUSED_VARIABLE")
    val handleTagsContinue = { onboardingStep = 2 }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        when (onboardingStep) {
            0 -> WelcomeScreen(onContinue = handleWelcomeContinue)
            // The original layout passes an empty handler here, so the
            // Tags screen's Continue button is intentionally a no-op.
            1 -> TagsScreen(onContinue = {})
            else -> AppTabs()
        }

        AnimatedSplashOverlay()
    }
}
