package com.jht.pointy.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jht.pointy.ui.viewModel.ScanViewModel

@Composable
fun ScanScreen(viewModel: ScanViewModel) {
    val scannedUid by viewModel.lastScannedUid.collectAsState()
    val activeCourseId by viewModel.activeCourseId.collectAsState()
    val activeCourseName by viewModel.activeCourseName.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitMessage by viewModel.submitMessage.collectAsState()
    val cs = MaterialTheme.colorScheme

    LaunchedEffect(scannedUid, activeCourseId) {
        val uid = scannedUid
        if (uid != null && activeCourseId != null) {
            viewModel.submitAttendanceFromCard(uid)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearOutSlowInEasing), RepeatMode.Restart),
        label = "wave1Scale"
    )
    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearOutSlowInEasing), RepeatMode.Restart),
        label = "wave1Alpha"
    )

    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 1000, easing = LinearOutSlowInEasing), RepeatMode.Restart),
        label = "wave2Scale"
    )
    val wave2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 1000, easing = LinearOutSlowInEasing), RepeatMode.Restart),
        label = "wave2Alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background),
        contentAlignment = Alignment.Center
    ) {
        if (activeCourseId == null && scannedUid == null) {
            EmptyScanState()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
            ) {

                if (activeCourseId != null) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = cs.primary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(bottom = 64.dp)
                    ) {
                        Text(
                            text = activeCourseName ?: "Mode présence",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = scannedUid != null,
                        transitionSpec = {
                            val enterAnim = fadeIn(tween(400)) + scaleIn(tween(400, easing = OvershootInterpolator().toEasing()))
                            val exitAnim = fadeOut(tween(200))
                            enterAnim.togetherWith(exitAnim)
                        },
                        label = "iconState"
                    ) { isScanned ->
                        if (isScanned) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Succès",
                                    modifier = Modifier.size(72.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .scale(wave1Scale)
                                        .border(2.dp, cs.primary.copy(alpha = wave1Alpha), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .scale(wave2Scale)
                                        .border(2.dp, cs.primary.copy(alpha = wave2Alpha), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(cs.primary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "En attente de carte",
                                        modifier = Modifier.size(48.dp),
                                        tint = cs.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                AnimatedContent(
                    targetState = scannedUid != null,
                    transitionSpec = {
                        fadeIn(tween(300)).togetherWith(fadeOut(tween(200))) // CORRECTION ICI
                    },
                    label = "title"
                ) { isScanned ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isScanned) "Étudiant pointé !" else "Prêt à scanner",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = if (isScanned) Color(0xFF4CAF50) else cs.onBackground
                        )
                        Spacer(Modifier.height(8.dp))

                        val statusText = when {
                            isScanned && isSubmitting -> "Enregistrement en cours..."
                            isScanned -> submitMessage ?: "Présence validée."
                            else -> "Approchez une carte au dos de l'appareil."
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = scannedUid != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cs.surface,
                        border = BorderStroke(1.dp, cs.outline.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "UID: $scannedUid",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = cs.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                AnimatedVisibility(
                    visible = scannedUid != null,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 },
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = { viewModel.resetScan() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.onBackground)
                    ) {
                        Text(
                            text = "SCAN SUIVANT",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = cs.background
                        )
                    }
                }
            }
        }
    }
}

fun android.view.animation.Interpolator.toEasing() = Easing { fraction ->
    this.getInterpolation(fraction)
}

@Composable
fun EmptyScanState() {
    val cs = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp).fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(cs.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = cs.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Aucun cours actif",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = cs.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Allez dans l'onglet 'Cours', sélectionnez un cours et appuyez sur 'Démarrer le scan'.",
            style = MaterialTheme.typography.bodyMedium,
            color = cs.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}