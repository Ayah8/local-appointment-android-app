package com.example.localappointmentmanager.data.repository

import com.example.localappointmentmanager.data.local.AppointmentDao
import com.example.localappointmentmanager.data.local.AppointmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * AppointmentRepositoryImpl is the concrete implementation of the repository.
 * It takes the DAO as a dependency.
 *
 * Why inherit from AppointmentRepository?
 * To fulfill the contract defined by the interface.
 */
class AppointmentRepositoryImpl(private val appointmentDao: AppointmentDao) : AppointmentRepository {

    /**
     * Delegates to the DAO's getAllAppointments().
     * No need for `suspend` because it returns a Flow (which is already async/reactive).
     */
    override fun getAllAppointments(): Flow<List<AppointmentEntity>> {
        return appointmentDao.getAllAppointments()
    }

    /**
     * Delegates to the DAO's getAppointmentById().
     * Marked as suspend to run off the main thread (though Room handles this safe-guarding too).
     */
    override suspend fun getAppointmentById(id: Int): AppointmentEntity? {
        return appointmentDao.getAppointmentById(id)
    }

    /**
     * Delegates insert.
     */
    override suspend fun insertAppointment(appointment: AppointmentEntity) {
        appointmentDao.insertAppointment(appointment)
    }

    /**
     * Delegates delete.
     */
    override suspend fun deleteAppointment(appointment: AppointmentEntity) {
        appointmentDao.deleteAppointment(appointment)
    }

    /**
     * Delegates update.
     */
    override suspend fun updateAppointment(appointment: AppointmentEntity) {
        appointmentDao.updateAppointment(appointment)
    }
}
