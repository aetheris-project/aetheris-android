package com.aetheris.android.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aetheris.android.ui.theme.AetherisColors
import com.aetheris.android.util.LanDiscovery
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectScreen(
    onServerSelected: (String) -> Unit,
    onConnectManually: (String) -> Unit
) {
    var showManualDialog by remember { mutableStateOf(false) }
    var manualUrl by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var discoveredServers by remember { mutableStateOf<List<LanDiscovery.DiscoveredServer>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AetherisColors.Accent.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background,
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                // Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AetherisColors.Accent.copy(alpha = 0.15f))
                        .border(2.dp, AetherisColors.Accent.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Aetheris",
                        tint = AetherisColors.Accent,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Aetheris",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Billing & Virtualization Panel",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AetherisColors.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Connect Manually Button
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showManualDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = AetherisColors.Surface.copy(alpha = 0.5f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AetherisColors.Border,
                                AetherisColors.Border.copy(alpha = 0.3f)
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = AetherisColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = "Connect Manually",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Enter your panel URL",
                                style = MaterialTheme.typography.bodySmall,
                                color = AetherisColors.TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = AetherisColors.TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LAN Discovery Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isScanning = true
                            scope.launch {
                                discoveredServers = LanDiscovery.discover()
                                isScanning = false
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AetherisColors.Accent
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = if (isScanning) "Scanning..." else "Discover on LAN",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Find Aetheris panels on your network",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Discovered servers
                if (discoveredServers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Discovered Panels",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    discoveredServers.forEach { server ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onServerSelected(server.url) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AetherisColors.Surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(AetherisColors.Success.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = AetherisColors.Success,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Text(
                                        text = server.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${server.ip}:${server.port}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AetherisColors.TextMuted
                                    )
                                }
                                if (server.version.isNotEmpty()) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "v${server.version}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AetherisColors.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Footer links
                Text(
                    text = "No panel? Install Aetheris on your server",
                    style = MaterialTheme.typography.bodySmall,
                    color = AetherisColors.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Manual connection dialog
    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Connect to Panel") },
            text = {
                Column {
                    Text(
                        text = "Enter your Aetheris panel URL",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AetherisColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manualUrl,
                        onValueChange = { manualUrl = it },
                        label = { Text("Server URL") },
                        placeholder = { Text("https://panel.example.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Home, contentDescription = null)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Example: https://panel.example.com or http://192.168.1.100",
                        style = MaterialTheme.typography.bodySmall,
                        color = AetherisColors.TextMuted
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (manualUrl.isNotBlank()) {
                            showManualDialog = false
                            onConnectManually(manualUrl.trim())
                        }
                    },
                    enabled = manualUrl.isNotBlank()
                ) {
                    Text("Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = AetherisColors.Surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = AetherisColors.TextSecondary
        )
    }
}
