package com.example.fittracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.ui.FitnessViewModel
import com.example.fittracker.ui.FitnessViewModelFactory
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class MainActivity : ComponentActivity() {
    private val db by lazy { AppDatabase.getDatabase(this) }
    private val viewModel: FitnessViewModel by viewModels { FitnessViewModelFactory(db.fitnessActivityDao()) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTrackerApp(viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FitTrackerApp(viewModel: FitnessViewModel) {
    var activityName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    val calendarState = rememberSelectableCalendarState()

    val activities by viewModel.activities.collectAsState()
    val selectedDate = calendarState.selectionState.selection.firstOrNull()

    val totalDuration by remember(selectedDate) {
        if (selectedDate != null) {
            viewModel.getTotalDurationForDate(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        } else {
            flowOf(0)
        }
    }.collectAsState(initial = 0)

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = activityName, onValueChange = { activityName = it }, label = { Text("Activity Name") })
        TextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (minutes)") })

        SelectableCalendar(calendarState = calendarState)

        Row {
            Button(
                onClick = {
                    if (selectedDate != null) {
                        viewModel.insert(activityName, duration, selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    }
                },
                enabled = activityName.isNotBlank() && duration.isNotBlank() && selectedDate != null
            ) {
                Text("Add")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (selectedDate != null) {
                    viewModel.getActivitiesByDate(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                }
            }) {
                Text("Filter")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.getAllActivities() }) {
                Text("Show All")
            }
        }

        if (selectedDate != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            Text("Date: ${dateFormat.format(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))}")
            Text("Total duration: ${totalDuration ?: 0} min")
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(activities) { activity ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                Text("${activity.activityName} - ${activity.duration} min (${dateFormat.format(Date(activity.date))})")
            }
        }
    }
}