package com.jht.pointy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.data.model.Course
import com.jht.pointy.data.network.SessionManager
import com.jht.pointy.ui.viewModel.DashboardUiState
import com.jht.pointy.ui.viewModel.DashboardViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(onProfileClick: () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme

    val greeting = when (java.time.LocalTime.now().hour) {
        in 5..11  -> "Bonjour"
        in 12..17 -> "Bon après-midi"
        else      -> "Bonsoir"
    }

    val firstName = SessionManager.teacherFirstName ?: ""
    val lastName  = SessionManager.teacherLastName ?: ""
    val initials  = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()

    TopAppBar(
        title = {
            Text(
                text = "$greeting, $firstName $lastName",
                style = MaterialTheme.typography.titleMedium
            )
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(36.dp)
                    .background(cs.primary, CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = cs.onPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        windowInsets = WindowInsets(0)
    )
}


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onCourseClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column {
        DashboardTopBar(onProfileClick = onProfileClick)
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