package com.ngocdevv.onboardingtags.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngocdevv.onboardingtags.R
import com.ngocdevv.onboardingtags.ui.components.ContinueButton
import com.ngocdevv.onboardingtags.ui.components.Entering
import com.ngocdevv.onboardingtags.ui.components.entering

private data class Feature(
    val icon: ImageVector,
    val color: Color,
    val iconColor: Color,
    val title: String,
    val description: String,
)

// Icon families from the original (Ionicons / MaterialCommunityIcons)
// are mapped to Material icons.
private val FEATURES = listOf(
    Feature(
        icon = Icons.Outlined.Search,
        color = Color(0xFFE8F0FE),
        iconColor = Color(0xFF4A90D9),
        title = "Find new activities",
        description = "Discover fresh ideas that spark your interest",
    ),
    Feature(
        icon = Icons.Outlined.Eco,
        color = Color(0xFFE8F5E9),
        iconColor = Color(0xFF4CAF50),
        title = "Stay calm and mindful",
        description = "Find new activities that spark your interest",
    ),
    Feature(
        icon = Icons.Outlined.Lightbulb,
        color = Color(0xFFFFF3E0),
        iconColor = Color(0xFFFF9800),
        title = "Boost your creativity",
        description = "Stay calm and mindful while exploring habits",
    ),
    Feature(
        icon = Icons.Outlined.EmojiEvents,
        color = Color(0xFFF3E5F5),
        iconColor = Color(0xFF9C27B0),
        title = "Build lasting routines",
        description = "Boost your creativity with daily inspiration",
    ),
)

/** Port of `src/components/WelcomeScreen.tsx`. */
@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 16.dp),
    ) {
        // Rocket Illustration
        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.rocket),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
                    .entering(Entering.ZoomIn, durationMillis = 1000, springify = false),
            )
        }

        // Feature List
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterVertically),
        ) {
            FEATURES.forEachIndexed { index, feature ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .entering(Entering.FadeInDown, delayMillis = 300 + index * 200),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        Modifier
                            .padding(top = 2.dp)
                            .size(44.dp)
                            .background(feature.color, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = feature.icon,
                            contentDescription = null,
                            tint = feature.iconColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = feature.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            letterSpacing = (-0.2).sp,
                        )
                        Text(
                            text = feature.description,
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
        }

        // Continue Button
        ContinueButton(
            onClick = onContinue,
            modifier = Modifier
                .padding(top = 16.dp)
                .entering(Entering.FadeInUp, delayMillis = 1400),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    WelcomeScreen(onContinue = {})
}
