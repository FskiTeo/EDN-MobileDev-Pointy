package com.jht.pointy.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.data.model.Teacher
import com.jht.pointy.ui.viewModel.ProfileState
import com.jht.pointy.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val cs = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label = "profileState"
        ) { state ->
            when (state) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        color       = cs.primary,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(32.dp)
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text       = "Erreur de chargement",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = cs.error
                        )
                        Text(
                            text     = state.message,
                            fontSize = 13.sp,
                            color    = cs.onSurfaceVariant
                        )
                        Button(
                            onClick   = { viewModel.fetchProfile() },
                            shape     = RoundedCornerShape(8.dp),
                            colors    = ButtonDefaults.buttonColors(containerColor = cs.onBackground),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text("Réessayer", color = cs.background)
                        }
                    }
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        teacher = state.teacher,
                        onLogoutClick = onLogoutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    teacher: Teacher,
    onLogoutClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(cs.primary.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, cs.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "${teacher.firstName.first()}${teacher.lastName.first()}",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Black,
                color      = cs.primary
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text          = "${teacher.firstName} ${teacher.lastName}",
            fontSize      = 28.sp,
            fontWeight    = FontWeight.Black,
            color         = cs.onBackground,
            letterSpacing = (-0.5).sp
        )
        Text(
            text       = "Enseignant",
            fontSize   = 14.sp,
            color      = cs.onSurfaceVariant
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text          = "INFORMATIONS",
            fontSize      = 10.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = cs.onSurfaceVariant,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(12.dp))

        InfoRow(icon = Icons.Default.Email, label = "Email", value = teacher.email)

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = cs.error)
        ) {
            Text(
                text = "Se déconnecter",
                color = cs.onError,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cs.surface)
            .border(1.dp, cs.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = cs.primary,
            modifier           = Modifier.size(18.dp)
        )
        Column {
            Text(
                text          = label,
                fontSize      = 10.sp,
                color         = cs.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                fontWeight    = FontWeight.Medium
            )
            Text(
                text       = value,
                fontSize   = 14.sp,
                color      = cs.onSurface
            )
        }
    }
}