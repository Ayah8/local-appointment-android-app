package com.example.localappointmentmanager.data.repository

import com.example.localappointmentmanager.data.local.AppointmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * AppointmentRepository defines the contract for data operations.
 * It abstracts the data source (Room) from the rest of the app (ViewModel).
 *
 * Why an Interface?
 * 1. Testability: We can easily create a MockRepository for unit testing ViewModels.
 * 2. Maintainability: If we switch to a different DB or add a network layer later,
 *    we only change the implementation, not the ViewModels.
 */
interface AppointmentRepository {

    /**
     * Observes all appointments.
     * Returns a Flow that emits the latest list whenever the database changes.
     */
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    /**
     * Gets a single appointment by ID.
     */
    suspend fun getAppointmentById(id: Int): AppointmentEntity?

    /**
     * Inserts a new appointment.
     */
    suspend fun insertAppointment(appointment: AppointmentEntity)

    /**
     * Deletes an appointment.
     */
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    /**
     * Updates an appointment.
     */
    suspend fun updateAppointment(appointment: AppointmentEntity)
}
