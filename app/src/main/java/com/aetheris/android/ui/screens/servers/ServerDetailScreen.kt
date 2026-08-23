package com.aetheris.android.ui.screens.servers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.model.*
import com.aetheris.android.ui.theme.AetherisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDetailScreen(
    serverId: String,
    onConsoleClick: () -> Unit,
    onBack: () -> Unit
) {
    // Demo server data
    val server = remember {
        Server(
            id = serverId,
            name = "minecraft-prod-01",
            node = "Node-EU-1",
            status = ServerStatus.RUNNING,
            ip = "185.223.28.12",
            cpu = ResourceUsage(45, 100, 45f),
            memory = ResourceUsage(6144, 16384, 37.5f),
            disk = ResourceUsage(28, 50, 56f),
            game = "Minecraft 1.21",
            players = PlayerCount(42, 200),
            createdAt = "2026-01-15",
            owner = "admin@aetheris.dev"
        )
    }

    var showStopDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(server.name, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onConsoleClick) {
                        Icon(Icons.Filled.Terminal, contentDescription = "Console")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Status card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(getStatusColor(server.status).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Dns,
                                contentDescription = null,
                                tint = getStatusColor(server.status),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(server.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("${server.game} - ${server.node}", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // IP and address
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("IP Address", style = MaterialTheme.typography.labelSmall, color = AetherisColors.TextMuted)
                            Text(server.ip, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Players", style = MaterialTheme.typography.labelSmall, color = AetherisColors.TextMuted)
                            Text("${server.players.online}/${server.players.max}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resource usage
                    DetailResourceBar("CPU", server.cpu.percentage, "${server.cpu.current}/${server.cpu.max}%", AetherisColors.Info)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailResourceBar("Memory", server.memory.percentage, "${server.memory.current}/${server.memory.max} MB", AetherisColors.Accent)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailResourceBar("Disk", server.disk.percentage, "${server.disk.current}/${server.disk.max} GB", AetherisColors.Warning)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(
                            label = "Console",
                            icon = Icons.Filled.Terminal,
                            color = AetherisColors.Info,
                            onClick = onConsoleClick,
                            modifier = Modifier.weight(1f)
                        )
                        ActionButton(
                            label = "Restart",
                            icon = Icons.Filled.RestartAlt,
                            color = AetherisColors.Warning,
                            onClick = { showRestartDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(
                            label = "Suspend",
                            icon = Icons.Filled.Pause,
                            color = AetherisColors.TextSecondary,
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        )
                        ActionButton(
                            label = "Stop",
                            icon = Icons.Filled.Stop,
                            color = AetherisColors.Error,
                            onClick = { showStopDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Server info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Server Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Created", server.createdAt)
                    InfoRow("Owner", server.owner)
                    InfoRow("Game", server.game)
                    InfoRow("Node", server.node)
                    InfoRow("Status", server.status.name)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text("Stop Server") },
            text = { Text("Are you sure you want to stop ${server.name}? All players will be disconnected.") },
            confirmButton = {
                TextButton(onClick = { showStopDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = AetherisColors.Error)) {
                    Text("Stop")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) { Text("Cancel") }
            },
            containerColor = AetherisColors.Surface
        )
    }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("Restart Server") },
            text = { Text("Are you sure you want to restart ${server.name}? All players will be temporarily disconnected.") },
            confirmButton = {
                TextButton(onClick = { showRestartDialog = false }) { Text("Restart") }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) { Text("Cancel") }
            },
            containerColor = AetherisColors.Surface
        )
    }
}

@Composable
private fun DetailResourceBar(label: String, percentage: Float, text: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = AetherisColors.TextMuted, modifier = Modifier.width(50.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = when { percentage > 85f -> AetherisColors.Error; percentage > 60f -> AetherisColors.Warning; else -> color },
            trackColor = AetherisColors.Border,
        )
        Text(text, style = MaterialTheme.typography.labelSmall, color = AetherisColors.TextSecondary, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = color.copy(alpha = 0.1f)),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted, modifier = Modifier.width(80.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

private fun getStatusColor(status: ServerStatus) = when (status) {
    ServerStatus.RUNNING -> AetherisColors.Success
    ServerStatus.STOPPED -> AetherisColors.TextMuted
    ServerStatus.STARTING -> AetherisColors.Warning
    ServerStatus.STOPPING -> AetherisColors.Warning
    ServerStatus.ERROR -> AetherisColors.Error
    ServerStatus.SUSPENDED -> AetherisColors.Error
}
