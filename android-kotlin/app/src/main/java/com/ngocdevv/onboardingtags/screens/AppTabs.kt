package com.ngocdevv.onboardingtags.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ngocdevv.onboardingtags.physics.PhysicsWorld
import com.ngocdevv.onboardingtags.ui.theme.palette

/** Port of `src/components/app-tabs.tsx` (expo-router NativeTabs). */
@Composable
fun AppTabs() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val colors = palette()

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            NavigationBar(containerColor = colors.background) {
                val itemColors = NavigationBarItemDefaults.colors(
                    indicatorColor = colors.backgroundElement,
                    selectedTextColor = colors.text,
                    selectedIconColor = colors.text,
                    unselectedTextColor = colors.textSecondary,
                    unselectedIconColor = colors.textSecondary,
                )
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    label = { Text("Home") },
                    colors = itemColors,
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    colors = itemColors,
                )
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            when (selectedTab) {
                0 -> HomeScreen()
                else -> ExploreScreen()
            }
        }
    }
}

/** Port of `src/app/index.tsx`. */
@Composable
fun HomeScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        PhysicsWorld(Modifier.fillMaxSize())
    }
}
