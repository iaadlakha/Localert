package com.example.localert_app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.localert_app.data.dao.LocationDao
import com.example.localert_app.data.dao.NoteDao
import com.example.localert_app.data.dao.ReminderDao
import com.example.localert_app.data.entity.LocationHistory
import com.example.localert_app.data.entity.LocationPattern
import com.example.localert_app.data.entity.Note
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.util.DateConverter

@Database(
    entities = [
        Reminder::class,
        Note::class,
        LocationHistory::class,
        LocationPattern::class
    ],
    version = 2
)
@TypeConverters(DateConverter::class)
abstract class LocalertDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun noteDao(): NoteDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: LocalertDatabase? = null

        fun getDatabase(context: Context): LocalertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalertDatabase::class.java,
                    "localert_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Check if the column already exists
                val cursor = database.query("PRAGMA table_info(reminders)")
                var columnExists = false
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    if (columnName == "isActive") {
                        columnExists = true
                        break
                    }
                }
                cursor.close()

                // Only add the column if it doesn't exist
                if (!columnExists) {
                    database.execSQL("ALTER TABLE reminders ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
                }
            }
        }
    }
} 