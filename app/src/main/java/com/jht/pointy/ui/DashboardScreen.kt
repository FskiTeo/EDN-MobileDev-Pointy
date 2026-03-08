package com.jht.pointy.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.data.model.Course
import com.jht.pointy.data.network.SessionManager
import com.jht.pointy.ui.viewModel.DashboardUiState
import com.jht.pointy.ui.viewModel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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
                    .clip(CircleShape)
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
    val cs = MaterialTheme.colorScheme

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    //genère une liste de jours
    val dateList = remember {
        val today = LocalDate.now()
        (-3..14).map { today.plusDays(it.toLong()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        // Ajout de la Top Bar ici
        DashboardTopBar(onProfileClick = onProfileClick)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = cs.primary)
                    }
                }

                is DashboardUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = cs.error
                            )
                            Button(
                                onClick = { viewModel.loadCourses() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Réessayer", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                is DashboardUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cs.background)
                                .padding(top = 24.dp, bottom = 8.dp)
                        ) {
                            Text(
                                text = "Vos cours",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp
                                ),
                                color = cs.onBackground,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 16.dp)
                            )

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(dateList) { date ->
                                    DatePill(
                                        date = date,
                                        isSelected = date == selectedDate,
                                        onClick = { selectedDate = date }
                                    )
                                }
                            }
                        }

                        val filteredCourses = state.courses.filter {
                            it.startDateTime.toLocalDate() == selectedDate
                        }

                        AnimatedContent(
                            targetState = filteredCourses,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            label = "course_list_animation"
                        ) { coursesToShow ->
                            if (coursesToShow.isEmpty()) {
                                // État vide si aucun cours ce jour-là
                                EmptyStateView()
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(coursesToShow, key = { it.id }) { course ->
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
            }
        }
    }
}

@Composable
fun DatePill(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val dayNumber = date.dayOfMonth.toString()

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) cs.primary else cs.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) cs.onPrimary.copy(alpha = 0.8f) else cs.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dayNumber,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) cs.onPrimary else cs.onSurface
        )
    }
}

@Composable
fun EmptyStateView() {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = cs.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Journée libre",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = cs.onSurface
        )
        Text(
            text = "Aucun cours n'est programmé pour cette date.",
            style = MaterialTheme.typography.bodyMedium,
            color = cs.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun CourseItem(
    course: Course,
    onClick: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val endTime = course.startDateTime.plus(course.duration)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = cs.surface,
        border = BorderStroke(1.dp, cs.outline.copy(alpha = 0.1f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = course.startDateTime.format(formatter),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = cs.onSurface
                )
                Box(
                    Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .padding(vertical = 4.dp)
                        .background(cs.outlineVariant, RoundedCornerShape(50))
                )
                Text(
                    text = endTime.format(formatter),
                    color = cs.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = cs.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Lieu",
                        modifier = Modifier.size(14.dp),
                        tint = cs.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = course.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Voir",
                tint = cs.outline.copy(alpha = 0.5f)
            )
        }
    }
}