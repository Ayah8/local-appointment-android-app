package com.example.localappointmentmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase is the main entry point for the local SQLite database.
 *
 * @Database: Declares the entities (tables) and version number.
 * entities = [AppointmentEntity::class]: We currently have one table.
 * version = 1: The schema version. If we change the schema later (add columns), we must increment this.
 */
@Database(entities = [AppointmentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Expose the DAO to the rest of the app
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        // Volatile ensures that the value of Instance is immediately visible to all threads.
        @Volatile
        private var Instance: AppDatabase? = null

        /**
         * getDatabase returns the singleton instance of the database.
         * Why Singleton?
         * Creating a database instance is expensive. We only want one open connection
         * across the entire app lifecycle to prevent performance issues and memory leaks.
         */
        fun getDatabase(context: Context): AppDatabase {
            // synchronised ensures that multiple threads don't create multiple instances at once.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appointment_database" // Simple name for the DB file
                )
                // .fallbackToDestructiveMigration() // Strategy for schema changes (wipes data on upgrade)
                // For this simple app, we can uncomment this if we change schema often without migration scripts.
                .build()
                .also { Instance = it }
            }
        }
    }
}
