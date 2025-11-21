package com.example.fittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.ui.FitnessViewModel
import com.example.fittracker.ui.FitnessViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class SummaryActivity : ComponentActivity() {
    private val db by lazy { AppDatabase.getDatabase(this) }
    private val viewModel: FitnessViewModel by viewModels { FitnessViewModelFactory(db.fitnessActivityDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selecteddate = intent.getLongExtra("SELECTED_DATE", 0L)

        setContent {
            SummaryScreen(viewModel, selecteddate)
        }
    }
}

fun AppDatabase.Companion.getDatabase(activity: SummaryActivity) {}

@Composable
fun SummaryScreen(viewModel: FitnessViewModel, selecteddate: Long) {
    val totalduration by viewModel.getTotalDurationForDate(selecteddate).collectAsState(initial = 0)
    val dateformat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Summary for: ${dateformat.format(Date(selecteddate))}")
        Text(text = "Total Duration: ${totalduration ?: 0} minutes")
    }
}
