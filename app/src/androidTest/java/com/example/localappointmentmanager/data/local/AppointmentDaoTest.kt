package com.example.localappointmentmanager.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * AppointmentDaoTest performs integration tests on the Database and DAO.
 * It uses an in-memory database which is destroyed after the tests, so it doesn't affect real data.
 *
 * @RunWith(AndroidJUnit4::class): Tells JUnit to run this using the AndroidJUnit4 runner.
 */
@RunWith(AndroidJUnit4::class)
class AppointmentDaoTest {

    private lateinit var appointmentDao: AppointmentDao
    private lateinit var db: AppDatabase

    /**
     * @Before: Runs before each test case.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Create an in-memory DB for testing.
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        appointmentDao = db.appointmentDao()
    }

    /**
     * @After: Runs after each test case.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Verifies that inserting an appointment and retrieving it works correctly.
     * Use runBlocking for coroutine support in tests.
     */
    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val appointment = AppointmentEntity(
            clientName = "John Doe",
            dateTimestamp = 1700000000L,
            timeString = "10:00",
            notes = "Test Note",
            status = "Scheduled"
        )
        
        appointmentDao.insertAppointment(appointment)
        
        // We use .first() to get the first emission from the Flow
        val byDate = appointmentDao.getAllAppointments().first()
        
        assertEquals(1, byDate.size)
        assertEquals("John Doe", byDate[0].clientName)
    }
}
