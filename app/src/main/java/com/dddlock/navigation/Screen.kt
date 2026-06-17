package com.dddlock.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Início",
        icon = Icons.Default.Home
    )

    data object BlockedNumbers : Screen(
        route = "blocked",
        title = "Números",
        icon = Icons.Default.Block
    )

    data object Diagnosis : Screen(
        route = "diagnosis",
        title = "Diagnóstico",
        icon = Icons.Default.BugReport
    )

    data object About : Screen(
        route = "about",
        title = "Sobre",
        icon = Icons.Default.Info
    )

    companion object {
        val bottomNavItems = listOf(Home, BlockedNumbers, Diagnosis, About)
    }
}
