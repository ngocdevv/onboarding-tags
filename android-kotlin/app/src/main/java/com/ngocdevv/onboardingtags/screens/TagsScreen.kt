package com.ngocdevv.onboardingtags.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngocdevv.onboardingtags.physics.PhysicsWorld
import com.ngocdevv.onboardingtags.ui.components.ContinueButton
import com.ngocdevv.onboardingtags.ui.components.Entering
import com.ngocdevv.onboardingtags.ui.components.entering

/** Port of `src/components/TagsScreen.tsx`. */
@Composable
fun TagsScreen(onContinue: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
        ) {
            PhysicsWorld(Modifier.fillMaxSize())
        }

        ContinueButton(
            onClick = onContinue,
            modifier = Modifier
                .entering(Entering.FadeInDown, delayMillis = 600)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
        )
    }
}
