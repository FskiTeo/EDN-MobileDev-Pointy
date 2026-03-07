package com.jht.pointy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.ui.viewModel.CourseViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    courseId: String,
    onBack: () -> Unit = {},
    onStartScanClick: (courseId: String, courseName: String) -> Unit = { _, _ -> },
    viewModel: CourseViewModel = viewModel()
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val updatingStudentId by viewModel.updatingStudentId.collectAsState()
    val courseName by viewModel.courseName.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.loadStudents(courseId)
    }

    Column{
        TopAppBar(
            title = { Text(courseName.ifBlank { "Feuille d'appel" }) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            },
            windowInsets = WindowInsets(0)
        )
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Erreur",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadStudents(courseId) }) {
                            Text("Réessayer")
                        }
                    }
                }
            }

            else -> {
                Column {
                    Button(
                        onClick = { onStartScanClick(courseId, courseName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text("Démarrer le scan de présence")
                    }

                    LazyColumn {
                        items(students) { student ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .clickable(enabled = updatingStudentId != student.id) {
                                        viewModel.rotateAttendance(courseId, student)
                                    },
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${student.firstName} ${student.lastName}",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    AttendanceBadge(attendance = student.attendance)

                                    if (updatingStudentId == student.id) {
                                        Text(
                                            text = "Mise à jour...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun attendanceLabel(attendance: String): String {
    return when (attendance.lowercase()) {
        "present" -> "✅ Présent"
        "excused" -> "🟡 Excusé"
        else -> "❌ Absent"
    }
}

@Composable
private fun AttendanceBadge(attendance: String) {
    val cs = MaterialTheme.colorScheme
    val (bgColor, textColor) = when (attendance.lowercase()) {
        "present" -> cs.primary.copy(alpha = 0.14f) to cs.primary
        "excused" -> Color(0xFFFFA000).copy(alpha = 0.16f) to Color(0xFF8A5300)
        else -> cs.error.copy(alpha = 0.14f) to cs.error
    }

    Box(
        modifier = Modifier
            .padding(top = 6.dp)
            .background(bgColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = attendanceLabel(attendance),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}