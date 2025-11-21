package com.example.fittracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessActivityDao {

    @Insert
    suspend fun insert(fitnessActivity: FitnessActivity)

    @Query("SELECT * FROM fitness_activities ORDER BY date DESC")
    fun getAllActivities(): Flow<List<FitnessActivity>>

    @Query("SELECT * FROM fitness_activities WHERE date = :date ORDER BY date DESC")
    fun getActivitiesByDate(date: Long): Flow<List<FitnessActivity>>

    @Query("SELECT SUM(duration) FROM fitness_activities WHERE date = :date")
    fun getTotalDurationForDate(date: Long): Flow<Int?>
}