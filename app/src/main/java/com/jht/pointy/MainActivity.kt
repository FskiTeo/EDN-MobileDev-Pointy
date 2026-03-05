package com.jht.pointy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.jht.pointy.ui.attendance.AttendanceScreen
import com.jht.pointy.ui.theme.PointyTheme
import com.jht.pointy.ui.dashboard.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PointyTheme {
                PointyApp()
            }
        }
    }
}

@Composable
fun PointyApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.COURS) }
    var selectedCourseId by rememberSaveable { mutableStateOf<String?>(null) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = {
                        currentDestination = destination
                        selectedCourseId = null  // permet de reset quand on change d'onglet
                    }
                )
            }
        }
    ) {
        // Le contenu de l'écran change ici en fonction de la destination
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                when (currentDestination) {
                    AppDestinations.COURS -> {
                        val courseId = selectedCourseId
                        if (courseId == null) {
                            DashboardScreen(
                                onCourseClick = { id -> selectedCourseId = id }
                            )
                        } else {
                            AttendanceScreen(courseId = courseId)
                        }
                    }
                    AppDestinations.ELEVES -> {
                        PlaceholderScreen("Gestion des élèves & NFC")
                    }
                    AppDestinations.PROFIL -> {
                        // TODO: Remplacer par ProfileScreen()
                        PlaceholderScreen("Paramètres du professeur")
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    COURS("Cours", Icons.Default.DateRange),
    ELEVES("Élèves", Icons.Default.Person),
    PROFIL("Profil", Icons.Default.Settings),
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}