package com.example.localappointmentmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * AppointmentEntity represents a table in the local Room database.
 * Each instance of this class corresponds to a single row in the 'appointments' table.
 *
 * We use @Entity to tell Room that this class is a database table.
 * The tableName is set to "appointments" for clarity in SQL queries.
 */
@Entity(tableName = "appointments")
data class AppointmentEntity(
    /**
     * The unique ID for each appointment.
     * @PrimaryKey tells Room this is the primary key.
     * autoGenerate = true: Room will automatically generate unique IDs (1, 2, 3...) for us.
     * We set it to default '0', but Room ignores this when inserting new rows.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * The name of the client.
     */
    val clientName: String,

    /**
     * The date of the appointment.
     * We use Long (timestamp in milliseconds) because Room handles primitives efficiently.
     * Storing complex Date objects requires TypeConverters, which adds complexity.
     * Keeping it simple fits the "Local/Offline" scope.
     */
    val dateTimestamp: Long,

    /**
     * Although time is often part of the timestamp, storing a formatted string or separate
     * hour/minute can sometimes be easier for simple UI display if timezone logic isn't heavy.
     * Here we just store it as a string (e.g., "14:30") for simplicity as per requirements.
     */
    val timeString: String,

    /**
     * Function notes or details about the appointment.
     */
    val notes: String = "",

    /**
     * Status of the appointment (e.g., "Scheduled", "Completed").
     * Storing as a simple String to avoid Enum converters for now,
     * maintaining the strict "keep it simple" approach.
     */
    val status: String = "Scheduled"
)
