package com.jht.pointy

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jht.pointy.ui.LoginScreen
import com.jht.pointy.ui.ProfileScreen
import com.jht.pointy.ui.ScanScreen
import com.jht.pointy.ui.AttendanceScreen
import com.jht.pointy.ui.DashboardScreen
import com.jht.pointy.ui.theme.PointyTheme
import com.jht.pointy.ui.viewModel.AuthStateViewModel
import com.jht.pointy.ui.viewModel.AuthUiState
import com.jht.pointy.ui.viewModel.ScanViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Activity ─────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val scanViewModel by viewModels<ScanViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        enableEdgeToEdge()
        setContent {
            PointyTheme {
                PointyApp(scanViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val hexId = it.id.joinToString(":") { byte -> "%02X".format(byte) }
                scanViewModel.onCardScanned(hexId)
                Log.d("NFC_SCAN", "Carte détectée : $hexId")
                Toast.makeText(this, "Carte détectée : $hexId", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// ─── App Root ─────────────────────────────────────────────────────────────────

@Composable
fun PointyApp(scanViewModel: ScanViewModel) {
    val authViewModel: AuthStateViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.COURS) }
    var selectedCourseId   by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.bootstrapSession()
    }

    if (authState != AuthUiState.LoggedIn) {
        LoginScreen(onLoginSuccess = { authViewModel.onLoginSuccess() })
    } else {
        val cs = MaterialTheme.colorScheme

        Scaffold(
            modifier       = Modifier.fillMaxSize(),
            containerColor = cs.background,
            bottomBar = {
                PointyBottomBar(
                    current  = currentDestination,
                    onSelect = {
                        currentDestination = it
                        selectedCourseId   = null
                        scanViewModel.stopAttendanceScan()
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    AppDestinations.COURS -> {
                        val courseId = selectedCourseId
                        if (courseId == null) {
                            DashboardScreen(
                                onCourseClick = { id -> selectedCourseId = id },
                                onProfileClick = { currentDestination = AppDestinations.PROFIL }
                            )
                        } else {
                            AttendanceScreen(
                                courseId = courseId,
                                onBack = { selectedCourseId = null },
                                onStartScanClick = { id, name ->
                                    scanViewModel.startAttendanceScan(id, name)
                                    currentDestination = AppDestinations.ELEVES
                                }
                            )
                        }
                    }
                    AppDestinations.ELEVES -> ScanScreen(viewModel = scanViewModel)
                    AppDestinations.PROFIL -> ProfileScreen(
                        onLogoutClick = {
                            authViewModel.logout()
                            currentDestination = AppDestinations.COURS
                            selectedCourseId = null
                            scanViewModel.stopAttendanceScan()
                        }
                    )
                }
            }
        }
    }
}

// ─── Bottom Bar ───────────────────────────────────────────────────────────────

@Composable
fun PointyBottomBar(
    current: AppDestinations,
    onSelect: (AppDestinations) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.background)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(cs.surface)
                .border(
                    width = 1.dp,
                    color = cs.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppDestinations.entries.forEach { destination ->
                    NavItem(
                        destination = destination,
                        isSelected  = destination == current,
                        onClick     = { onSelect(destination) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    destination: AppDestinations,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val bgAlpha by animateFloatAsState(
        targetValue   = if (isSelected) 1f else 0f,
        animationSpec = tween(250, easing = EaseInOutCubic),
        label         = "bgAlpha"
    )
    val iconTint by animateColorAsState(
        targetValue   = if (isSelected) cs.onPrimary else cs.onSurfaceVariant,
        animationSpec = tween(250),
        label         = "iconTint"
    )
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(cs.primary.copy(alpha = bgAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = if (isSelected) 20.dp else 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isSelected,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
            label = "navContent"
        ) { selected ->
            if (selected) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector        = destination.icon,
                        contentDescription = destination.label,
                        tint               = iconTint,
                        modifier           = Modifier.size(18.dp)
                    )
                    Text(
                        text          = destination.label,
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = iconTint,
                        letterSpacing = 0.2.sp
                    )
                }
            } else {
                Icon(
                    imageVector        = destination.icon,
                    contentDescription = destination.label,
                    tint               = iconTint,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Destinations ─────────────────────────────────────────────────────────────

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    COURS("Cours", Icons.Default.DateRange),
    ELEVES("Élèves", Icons.Default.Person),
    PROFIL("Profil", Icons.Default.Settings),
}

// ─── Placeholder ──────────────────────────────────────────────────────────────

@Composable
fun PlaceholderScreen(text: String) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.headlineMedium,
            color      = cs.onSurfaceVariant,
            fontWeight = FontWeight.Light
        )
    }
}