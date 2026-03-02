package com.jht.pointy.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jht.pointy.ui.viewModel.ScanViewModel

@Composable
fun ScanScreen(viewModel: ScanViewModel) {
    val scannedUid by viewModel.lastScannedUid.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(targetState = scannedUid != null) { isScanned ->
            if (isScanned) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Succès",
                    modifier = Modifier.size(120.dp),
                    tint = Color(0xFF4CAF50) // Vert
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "En attente",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (scannedUid == null) "Prêt à scanner" else "Étudiant détecté !",
            style = MaterialTheme.typography.headlineSmall
        )

        if (scannedUid != null) {
            Text(
                text = "ID: $scannedUid",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Button(
                onClick = { viewModel.resetScan() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Scanner un autre élève")
            }
        } else {
            Text(
                text = "Approchez une carte NFC du dos du téléphone",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}