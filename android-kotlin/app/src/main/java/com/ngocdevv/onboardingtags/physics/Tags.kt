package com.ngocdevv.onboardingtags.physics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Port of `src/constants/Tags.ts`.
 * The original renders MaterialCommunityIcons glyphs through Skia;
 * here each tag maps to the closest Material icon.
 */
data class Tag(
    val id: String,
    val label: String,
    val color: Color,
    val icon: ImageVector,
)

val TAGS: List<Tag> = listOf(
    Tag("1", "Code", Color(0xFFFFE4E1), Icons.Outlined.Code),
    Tag("2", "Design", Color(0xFFFFFACD), Icons.Outlined.Brush),
    Tag("3", "Music", Color(0xFFE0FFFF), Icons.Outlined.MusicNote),
    Tag("4", "Travel", Color(0xFFF0E68C), Icons.Outlined.Flight),
    Tag("5", "Finance", Color(0xFFDDA0DD), Icons.Outlined.AccountBalanceWallet),
    Tag("6", "Cook", Color(0xFF90EE90), Icons.Outlined.Restaurant),
    Tag("7", "Meditate", Color(0xFFADD8E6), Icons.Outlined.SelfImprovement),
    Tag("8", "Work", Color(0xFFFFB6C1), Icons.Outlined.Laptop),
    Tag("9", "Learn", Color(0xFFFAFAD2), Icons.AutoMirrored.Outlined.MenuBook),
    Tag("10", "Exercise", Color(0xFF87CEFA), Icons.AutoMirrored.Outlined.DirectionsRun),
    Tag("11", "Photo", Color(0xFFD3D3D3), Icons.Outlined.PhotoCamera),
    Tag("12", "Garden", Color(0xFF98FB98), Icons.Outlined.LocalFlorist),
    Tag("13", "Explore", Color(0xFFAFEEEE), Icons.Outlined.Explore),
    Tag("14", "Write", Color(0xFFFFDEAD), Icons.Outlined.Edit),
    Tag("15", "Research", Color(0xFFE6E6FA), Icons.Outlined.Science),
)
