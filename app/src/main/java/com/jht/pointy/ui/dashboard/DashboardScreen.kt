package com.jht.pointy.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.data.model.Course
import com.jht.pointy.state.CourseState
import com.jht.pointy.ui.viewModel.CourseViewModel

@Composable
fun DashboardScreen(
    viewModel: CourseViewModel = viewModel(),
    onCourseClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is CourseState.Idle,
        is CourseState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CourseState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is CourseState.Success -> {
            LazyColumn {
                items(state.courses) { course ->
                    CourseItem(
                        course  = course,
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
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        onClick   = onClick,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = course.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text  = course.location,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text  = course.startDateTime,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}