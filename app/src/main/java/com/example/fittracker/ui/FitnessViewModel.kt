package com.example.fittracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.FitnessActivity
import com.example.fittracker.data.FitnessActivityDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FitnessViewModel(private val dao: FitnessActivityDao) : ViewModel() {

    private val _activities = MutableStateFlow<List<FitnessActivity>>(emptyList())
    val activities: StateFlow<List<FitnessActivity>> = _activities.asStateFlow()

    init {
        getAllActivities()
    }

    fun insert(activityName: String, duration: String, date: Long) {
        val durationInt = duration.toIntOrNull() ?: 0
        val activity = FitnessActivity(
            activityName = activityName,
            duration = durationInt,
            date = date
        )
        viewModelScope.launch {
            dao.insert(activity)
        }
    }

    fun getAllActivities() {
        viewModelScope.launch {
            dao.getAllActivities().collect {
                _activities.value = it
            }
        }
    }

    fun getActivitiesByDate(date: Long) {
        viewModelScope.launch {
            dao.getActivitiesByDate(date).collect {
                _activities.value = it
            }
        }
    }

    fun getTotalDurationForDate(date: Long): Flow<Int?> {
        return dao.getTotalDurationForDate(date)
    }
}

class FitnessViewModelFactory(private val dao: FitnessActivityDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitnessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FitnessViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}