package com.example.localert_app.data.dao

import androidx.room.*
import com.example.localert_app.data.entity.LocationHistory
import com.example.localert_app.data.entity.LocationPattern

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocationHistory(location: LocationHistory)
    
    @Insert
    suspend fun insertLocationPattern(pattern: LocationPattern)
    
    @Query("SELECT * FROM location_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLocations(limit: Int): List<LocationHistory>
    
    @Query("SELECT * FROM location_patterns ORDER BY frequency DESC")
    suspend fun getLocationPatterns(): List<LocationPattern>
} 