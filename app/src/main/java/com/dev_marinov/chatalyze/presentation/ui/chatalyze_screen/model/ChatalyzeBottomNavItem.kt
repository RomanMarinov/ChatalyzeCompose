package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ChatalyzeBottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)
