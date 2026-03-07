package com.jht.pointy.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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

    val pulse = rememberInfiniteTransition(label = "pulse")
    val ringScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue  = 1.15f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background),
        contentAlignment = Alignment.Center
    ) {
        if (activeCourseId == null && scannedUid == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Aucun cours sélectionné",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onBackground
                    )
                    Text(
                        text = "Sélectionnez un cours depuis l'onglet Cours pour démarrer le scan de présence",
                        fontSize = 14.sp,
                        color = cs.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            return
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                if (activeCourseId != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = activeCourseName?.let { "Cours : $it" } ?: "Mode présence cours actif",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = cs.primary,
                    )
                }

                Spacer(Modifier.height(72.dp))

                AnimatedContent(
                    targetState = scannedUid != null,
                    transitionSpec = {
                        fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.85f) togetherWith
                                fadeOut(tween(200))
                    },
                    label = "iconState"
                ) { isScanned ->
                    if (isScanned) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(cs.primary.copy(alpha = 0.08f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Succès",
                                modifier = Modifier.size(56.dp),
                                tint = cs.primary
                            )
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(ringScale)
                                    .border(
                                        width = 1.dp,
                                        color = cs.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(cs.primary.copy(alpha = 0.06f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "En attente NFC",
                                    modifier = Modifier.size(40.dp),
                                    tint = cs.primary
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))

                AnimatedContent(
                    targetState = scannedUid != null,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "title"
                ) { isScanned ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isScanned) "Étudiant détecté" else "Prêt à scanner",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = cs.onBackground,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = when {
                                isScanned && activeCourseId != null && isSubmitting -> "Enregistrement de la présence..."
                                isScanned && activeCourseId != null -> submitMessage
                                    ?: "Scan NFC réussi"

                                isScanned -> "Scan NFC réussi"
                                else -> "Approchez une carte NFC"
                            },
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                AnimatedVisibility(
                    visible = scannedUid != null,
                    enter = fadeIn(tween(400)) + expandVertically(tween(400)),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .background(cs.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, cs.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "ID : $scannedUid",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = cs.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))

                AnimatedVisibility(
                    visible = scannedUid != null,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    Button(
                        onClick = { viewModel.resetScan() },
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cs.onBackground
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = "Scanner un autre élève",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.background,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }
    }
}