package com.example.localert_app.di

import android.content.Context
import androidx.room.Room
import com.example.localert_app.data.database.LocalertDatabase
import com.example.localert_app.data.dao.ReminderDao
import com.example.localert_app.data.dao.NoteDao
import com.example.localert_app.data.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LocalertDatabase {
        return Room.databaseBuilder(
            context,
            LocalertDatabase::class.java,
            "localert_database"
        )
        .addMigrations(LocalertDatabase.MIGRATION_1_2)
        .build()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: LocalertDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: LocalertDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: LocalertDatabase): LocationDao {
        return database.locationDao()
    }
} 