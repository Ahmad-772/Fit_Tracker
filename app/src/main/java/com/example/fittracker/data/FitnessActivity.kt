package com.example.fittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_activities")
data class FitnessActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityName: String,
    val duration: Int,
    val date: Long
)