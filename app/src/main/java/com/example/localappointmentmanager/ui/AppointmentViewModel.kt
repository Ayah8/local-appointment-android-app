package com.example.localappointmentmanager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.localappointmentmanager.data.local.AppDatabase
import com.example.localappointmentmanager.data.local.AppointmentEntity
import com.example.localappointmentmanager.data.repository.AppointmentRepository
import com.example.localappointmentmanager.data.repository.AppointmentRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * AppointmentViewModel manages the UI state and business logic for appointments.
 * It interacts with the repository to fetch and save data.
 *
 * @param repository The data source.
 */
class AppointmentViewModel(private val repository: AppointmentRepository) : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)
    
    // Public immutable state observed by the UI
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    /**
     * Loads appointments from the repository.
     * Collects the Flow and updates the UI state.
     */
    private fun loadAppointments() {
        viewModelScope.launch {
            repository.getAllAppointments()
                .catch { e ->
                    // Handle errors during emission
                    _uiState.value = AppointmentUiState.Error(e.message ?: "Unknown error")
                }
                .collect { list ->
                    if (list.isEmpty()) {
                        _uiState.value = AppointmentUiState.Empty
                    } else {
                        _uiState.value = AppointmentUiState.Success(list)
                    }
                }
        }
    }

    /**
     * Updates an existing appointment.
     */
    fun updateAppointment(id: Int, name: String, date: Long, time: String, notes: String) {
        viewModelScope.launch {
             val updated = AppointmentEntity(
                id = id,
                clientName = name,
                dateTimestamp = date,
                timeString = time,
                notes = notes
            )
            repository.updateAppointment(updated)
        }
    }

    /**
     * Marks an appointment as completed.
     */
    fun markAppointmentCompleted(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.updateAppointment(appointment.copy(status = "Completed"))
        }
    }

    /**
     * Suspended function to get a single appointment.
     * Useful for Details/Edit screens.
     */
    suspend fun getAppointmentById(id: Int): AppointmentEntity? {
        return repository.getAppointmentById(id)
    }

    /**
     * Adds a new appointment.
     * Performs validation before saving.
     */
    fun addAppointment(name: String, date: Long, time: String, notes: String) {
        // Validation: Name must not be empty
        if (name.isBlank()) {
            // In a real app, we might emit a "ValidationError" event. 
            // For now, we just ignore or log it to keep simple.
            return
        }

        viewModelScope.launch {
            val newAppointment = AppointmentEntity(
                clientName = name,
                dateTimestamp = date,
                timeString = time,
                notes = notes
            )
            repository.insertAppointment(newAppointment)
        }
    }

    /**
     * Deletes an appointment.
     */
    fun deleteAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.deleteAppointment(appointment)
        }
    }

    /**
     * Factory object to allowing creating this ViewModel with dependencies.
     * This setup allows the UI to just call `viewModel()` without manual wiring every time.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the Application object from extras
                val application = (this[APPLICATION_KEY] as android.app.Application)
                // Get the database instance
                val database = AppDatabase.getDatabase(application)
                // Create repository
                val repository = AppointmentRepositoryImpl(database.appointmentDao())
                // Return ViewModel
                AppointmentViewModel(repository)
            }
        }
    }
}
