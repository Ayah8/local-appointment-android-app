package com.example.localappointmentmanager.ui

import com.example.localappointmentmanager.data.local.AppointmentEntity

/**
 * AppointmentUiState represents the various states of the UI.
 * We use a sealed interface to restrict the possible states to a known set.
 *
 * States:
 * - Loading: Data is being fetched.
 * - Success: Data fetched successfully (contains list).
 * - Empty: Data fetched but list is empty (can show "No appointments" view).
 * - Error: Something went wrong.
 */
sealed interface AppointmentUiState {
    object Loading : AppointmentUiState
    data class Success(val appointments: List<AppointmentEntity>) : AppointmentUiState
    object Empty : AppointmentUiState
    data class Error(val message: String) : AppointmentUiState
}
