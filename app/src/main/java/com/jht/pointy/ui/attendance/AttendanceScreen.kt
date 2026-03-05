package com.jht.pointy.ui.attendance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.ui.viewModel.CourseViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AttendanceScreen(
    courseId: String,
    viewModel: CourseViewModel = viewModel()
) {
    val students by viewModel.students.collectAsState()
    LaunchedEffect(courseId) {
        viewModel.loadStudents(courseId)
    }

    LazyColumn {
        items(students) { student ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${student.firstName} ${student.lastName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (student.isPresent) "✅ Présent" else "❌ Absent",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}