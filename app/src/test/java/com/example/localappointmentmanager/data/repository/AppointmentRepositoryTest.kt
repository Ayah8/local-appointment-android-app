package com.example.localappointmentmanager.data.repository

import com.example.localappointmentmanager.data.local.AppointmentDao
import com.example.localappointmentmanager.data.local.AppointmentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * AppointmentRepositoryTest verifies the business logic of the repository.
 * Since the generic RepositoryImpl is mostly a pass-through, this test ensures
 * the delegation is wired correctly using a Fake DAO.
 */
class AppointmentRepositoryTest {

    // A simple Fake DAO implementation for testing.
    // This avoids needing heavy mocking libraries like Mockito.
    class FakeAppointmentDao : AppointmentDao {
        val appointments = mutableListOf<AppointmentEntity>()

        override suspend fun insertAppointment(appointment: AppointmentEntity) {
            appointments.add(appointment)
        }

        override suspend fun updateAppointment(appointment: AppointmentEntity) {
            // Not needed for this specific test
        }

        override suspend fun deleteAppointment(appointment: AppointmentEntity) {
            appointments.remove(appointment)
        }

        override fun getAllAppointments(): Flow<List<AppointmentEntity>> {
            return flowOf(appointments)
        }

        override suspend fun getAppointmentById(id: Int): AppointmentEntity? {
            return appointments.find { it.id == id }
        }
    }

    @Test
    fun repository_inserts_to_dao() = runBlocking {
        // Given
        val fakeDao = FakeAppointmentDao()
        val repository = AppointmentRepositoryImpl(fakeDao)
        val appointment = AppointmentEntity(1, "Test Client", 123L, "10:00")

        // When
        repository.insertAppointment(appointment)

        // Then
        assertEquals(1, fakeDao.appointments.size)
        assertEquals("Test Client", fakeDao.appointments[0].clientName)
    }
}
