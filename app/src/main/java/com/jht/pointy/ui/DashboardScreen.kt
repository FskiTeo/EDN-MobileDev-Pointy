package com.jht.pointy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.data.model.Course
import com.jht.pointy.ui.viewModel.DashboardUiState
import com.jht.pointy.ui.viewModel.DashboardViewModel
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onCourseClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is DashboardUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DashboardUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadCourses() }) {
                        Text("Réessayer")
                    }
                }
            }
        }

        is DashboardUiState.Success -> {
            LazyColumn {
                items(state.courses) { course ->
                    CourseItem(
                        course = course,
                        onClick = { onCourseClick(course.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseItem(
    course: Course,
    onClick: () -> Unit = {}
) {

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val endTime = course.startDateTime.plus(course.duration)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = course.location)

            Text(
                text = "${course.startDateTime.format(formatter)} - ${
                    endTime.format(formatter)
                }"
            )
        }
    }
}