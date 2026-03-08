package com.jht.pointy.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.ui.viewModel.CourseViewModel

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

    val cs = MaterialTheme.colorScheme

    LaunchedEffect(courseId) {
        viewModel.loadStudents(courseId)
    }

    Scaffold(
        containerColor = cs.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = courseName.ifBlank { "Feuille d'appel" },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Gestion des présences",
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.background,
                    scrolledContainerColor = cs.surface
                ),
                windowInsets = WindowInsets(0)
            )
        },
        floatingActionButton = {
            if (!isLoading && errorMessage == null) {
                ExtendedFloatingActionButton(
                    onClick = { onStartScanClick(courseId, courseName) },
                    containerColor = cs.onBackground,
                    contentColor = cs.background,
                    shape = RoundedCornerShape(20.dp),
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("DÉMARRER LE SCAN", fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }
            }
        }
    ) { innerPadding ->

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = cs.primary)
                }
            }

            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Erreur inattendue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.error
                        )
                        Button(
                            onClick = { viewModel.loadStudents(courseId) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Réessayer", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(students) { student ->
                        val isUpdating = updatingStudentId == student.id
                        StudentCard(
                            firstName = student.firstName,
                            lastName = student.lastName,
                            attendance = student.attendance,
                            isUpdating = isUpdating
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(
    firstName: String,
    lastName: String,
    attendance: String,
    isUpdating: Boolean
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cs.surface,
        border = BorderStroke(0.5.dp, cs.outline.copy(alpha = 0.15f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$firstName $lastName",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = cs.onSurface
                )

                if (isUpdating) {
                    Text(
                        text = "Scan en cours...",
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    AttendanceBadge(attendance = attendance)
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
        "present" -> cs.primary.copy(alpha = 0.12f) to cs.primary
        "excused" -> Color(0xFFFFA000).copy(alpha = 0.12f) to Color(0xFFB07D00)
        else -> cs.error.copy(alpha = 0.12f) to cs.error
    }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(bgColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = attendanceLabel(attendance),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}