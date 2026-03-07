package com.jht.pointy.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.data.model.CourseStudent
import com.jht.pointy.state.CourseState
import com.jht.pointy.ui.viewModel.CourseViewModel

@Composable
fun AttendanceScreen(
    courseId: String,
    viewModel: CourseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val courseStudents = when (val state = uiState) {
        is CourseState.Success -> state.courses
            .find { it.id == courseId }
            ?.courseStudents ?: emptyList()
        else -> emptyList()
    }

    LazyColumn {
        items(courseStudents) { courseStudent ->
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text  = "${courseStudent.student.firstName} ${courseStudent.student.lastName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text  = courseStudent.student.cardSerial ?: "Pas de carte",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text  = if (courseStudent.attendance == "present") "✅" else "❌",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}