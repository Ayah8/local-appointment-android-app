package com.example.localappointmentmanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * AppointmentDao defines the database interactions.
 * It provides methods to Insert, Update, Delete, and Query appointments.
 *
 * @Dao annotation tells Room that this interface is a Data Access Object.
 * Room will automatically generate the implementation of this class at compile time.
 */
@Dao
interface AppointmentDao {

    /**
     * Inserts a new appointment into the database.
     * @param appointment The entity to save.
     * onConflict = OnConflictStrategy.REPLACE: If an item with the same ID exists, replace it.
     * suspend: This function must be called from a coroutine (async).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    /**
     * Updates an existing appointment.
     * Room matches the entity by its PrimaryKey (id).
     */
    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    /**
     * Deletes a specific appointment.
     */
    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    /**
     * Retrieves all appointments, ordered by date ascending (soonest first).
     *
     * Returns a Flow<List<AppointmentEntity>>.
     * Why Flow?
     * Flow is a reactive stream. If the data in the database changes (e.g., a new item is added),
     * Room will automatically emit the new list to this Flow.
     * This allows the UI to update automatically without manual refreshing.
     */
    @Query("SELECT * FROM appointments ORDER BY dateTimestamp ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    /**
     * Retrieves a specific appointment by ID.
     * Useful for the "Edit" or "Details" screen.
     */
    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Int): AppointmentEntity?
}
