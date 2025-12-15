package com.example.localappointmentmanager.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.localappointmentmanager.ui.screens.AddEditAppointmentScreen
import com.example.localappointmentmanager.ui.screens.AppointmentDetailsScreen
import com.example.localappointmentmanager.ui.screens.AppointmentListScreen

/**
 * AppointmentApp is the root Composable for the UI.
 * It sets up the Navigation and holds the shared ViewModel.
 */
@Composable
fun AppointmentApp() {
    val navController = rememberNavController()
    
    // We instantiate the ViewModel here using our Factory.
    // This ensures it survives configuration changes and is shared across screens if needed.
    val viewModel: AppointmentViewModel = viewModel(factory = AppointmentViewModel.Factory)

    NavHost(navController = navController, startDestination = "list") {
        
        // Screen 1: Appointment List
        composable("list") {
            // Collect UI state from ViewModel
            val uiState by viewModel.uiState.collectAsState()
            
            AppointmentListScreen(
                uiState = uiState,
                onAddAppointmentClicked = { navController.navigate("add") },
                onAppointmentClicked = { id -> navController.navigate("details/$id") }
            )
        }

        // Screen 2: Add Appointment (New)
        composable("add") {
            AddEditAppointmentScreen(
                appointmentId = -1,
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        // Screen 2 (Reuse): Edit Appointment
        composable(
            route = "edit/{appointmentId}",
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: -1
            
            AddEditAppointmentScreen(
                appointmentId = appointmentId,
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        // Screen 3: Details
        composable(
            route = "details/{appointmentId}",
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: return@composable
            
            AppointmentDetailsScreen(
                appointmentId = appointmentId,
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() },
                onEditClicked = { id -> navController.navigate("edit/$id") },
                onDeleteClicked = { navController.popBackStack() }
            )
        }
    }
}
