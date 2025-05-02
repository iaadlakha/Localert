package com.example.localert_app.di

import android.content.Context
import com.example.localert_app.service.AlarmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    
    @Provides
    @Singleton
    fun provideAlarmService(
        @ApplicationContext context: Context
    ): AlarmService {
        return AlarmService(context)
    }
} 