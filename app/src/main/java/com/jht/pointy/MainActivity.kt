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
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jht.pointy.ui.LoginScreen
import com.jht.pointy.ui.ScanScreen
// import com.jht.pointy.ui.attendance.AttendanceScreen // TODO: à créer
import com.jht.pointy.ui.dashboard.DashboardScreen
import com.jht.pointy.ui.theme.PointyTheme
import com.jht.pointy.ui.viewModel.ScanViewModel

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
    var isLoggedIn         by rememberSaveable { mutableStateOf(false) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.COURS) }
    var selectedCourseId   by rememberSaveable { mutableStateOf<String?>(null) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
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
                        selectedCourseId   = null // reset navigation interne au changement d'onglet
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
                        if (selectedCourseId == null) {
                            DashboardScreen(
                                onCourseClick = { id -> selectedCourseId = id }
                            )
                        } else {
                            PlaceholderScreen("Appel du cours $selectedCourseId") // TODO: AttendanceScreen
                        }
                    }
                    AppDestinations.ELEVES -> ScanScreen(viewModel = scanViewModel)
                    AppDestinations.PROFIL -> PlaceholderScreen("Paramètres du professeur")
                }
            }
        }
    }
}

// ─── Bottom Bar ───────────────────────────────────────────────────────────────

@Composable
private fun PointyBottomBar(
    current: AppDestinations,
    onSelect: (AppDestinations) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.surface)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(cs.outline.copy(alpha = 0.3f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
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

@Composable
private fun NavItem(
    destination: AppDestinations,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    color = if (isSelected) cs.primary else cs.surface,
                    shape = RoundedCornerShape(50)
                )
        )
        Icon(
            imageVector        = destination.icon,
            contentDescription = destination.label,
            tint               = if (isSelected) cs.onBackground else cs.onSurfaceVariant,
            modifier           = Modifier.size(22.dp)
        )
        Text(
            text          = destination.label,
            fontSize      = 10.sp,
            fontWeight    = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color         = if (isSelected) cs.onBackground else cs.onSurfaceVariant,
            letterSpacing = 0.3.sp
        )
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