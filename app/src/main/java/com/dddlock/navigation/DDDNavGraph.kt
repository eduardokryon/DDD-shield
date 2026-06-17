package com.dddlock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dddlock.DDDViewModelFactory
import com.dddlock.ui.screens.about.AboutScreen
import com.dddlock.ui.screens.blocked.BlockedNumbersScreen
import com.dddlock.ui.screens.blocked.BlockedNumbersViewModel
import com.dddlock.ui.screens.diagnosis.DiagnosisScreen
import com.dddlock.ui.screens.diagnosis.DiagnosisViewModel
import com.dddlock.ui.screens.home.HomeScreen
import com.dddlock.ui.screens.home.HomeViewModel

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
@Composable
fun DDDNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModelFactory: DDDViewModelFactory
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(viewModel = viewModel)
        }
        composable(Screen.BlockedNumbers.route) {
            val viewModel: BlockedNumbersViewModel = viewModel(factory = viewModelFactory)
            BlockedNumbersScreen(viewModel = viewModel)
        }
        composable(Screen.Diagnosis.route) {
            val viewModel: DiagnosisViewModel = viewModel(factory = viewModelFactory)
            DiagnosisScreen(viewModel = viewModel)
        }
        composable(Screen.About.route) {
            AboutScreen()
        }
    }
}
