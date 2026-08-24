package com.aetheris.android.ui.screens.servers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.model.Server
import com.aetheris.android.data.model.ServerStatus
import com.aetheris.android.ui.theme.AetherisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    onServerClick: (String) -> Unit
) {
    var servers by remember { mutableStateOf(getDemoServers()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredServers = if (searchQuery.isBlank()) servers
    else servers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.game.contains(searchQuery, ignoreCase = true) ||
        it.node.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Servers",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${servers.count { it.status == ServerStatus.RUNNING }} of ${servers.size} running",
                            style = MaterialTheme.typography.bodySmall,
                            color = AetherisColors.TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search servers...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = AetherisColors.Border,
                        focusedBorderColor = AetherisColors.Accent,
                        cursorColor = AetherisColors.Accent
                    )
                )
            }

            items(filteredServers) { server ->
                ServerCard(server = server, onClick = { onServerClick(server.id) })
            }
        }
    }
}

@Composable
private fun ServerCard(
    server: Server,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AetherisColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: name + status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(getStatusColor(server.status).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getServerIcon(server.game),
                        contentDescription = null,
                        tint = getStatusColor(server.status),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = server.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${server.node} - ${server.game}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AetherisColors.TextMuted
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = getStatusColor(server.status).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = server.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = getStatusColor(server.status),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resource bars
            ResourceBar(label = "CPU", percentage = server.cpu.percentage, color = AetherisColors.Info)
            Spacer(modifier = Modifier.height(6.dp))
            ResourceBar(label = "MEM", percentage = server.memory.percentage, color = AetherisColors.Accent)
            Spacer(modifier = Modifier.height(6.dp))
            ResourceBar(label = "DSK", percentage = server.disk.percentage, color = AetherisColors.Warning)

            // Players
            if (server.players.max > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = AetherisColors.TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "  ${server.players.online}/${server.players.max} players",
                        style = MaterialTheme.typography.bodySmall,
                        color = AetherisColors.TextMuted
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = server.ip,
                        style = MaterialTheme.typography.labelSmall,
                        color = AetherisColors.TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourceBar(
    label: String,
    percentage: Float,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AetherisColors.TextMuted,
            modifier = Modifier.width(32.dp)
        )
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = getBarColor(percentage, color),
            trackColor = AetherisColors.Border,
        )
        Text(
            text = "${percentage.toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = AetherisColors.TextSecondary,
            modifier = Modifier.padding(start = 8.dp)
        )
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

private fun getBarColor(percentage: Float, baseColor: androidx.compose.ui.graphics.Color) = when {
    percentage > 85f -> AetherisColors.Error
    percentage > 60f -> AetherisColors.Warning
    else -> baseColor
}

private fun getServerIcon(game: String) = when {
    game.contains("Minecraft", true) -> Icons.Filled.List
    game.contains("Valheim", true) || game.contains("Rust", true) -> Icons.Filled.List
    game.contains("CS", true) || game.contains("Source", true) -> Icons.Filled.Settings
    else -> Icons.Filled.List
}

private fun getDemoServers() = listOf(
    Server(id = "1", name = "minecraft-prod-01", node = "Node-EU-1", status = ServerStatus.RUNNING, ip = "185.223.28.12", cpu = com.aetheris.android.data.model.ResourceUsage(45, 100, 45f), memory = com.aetheris.android.data.model.ResourceUsage(6144, 16384, 37.5f), disk = com.aetheris.android.data.model.ResourceUsage(28, 50, 56f), game = "Minecraft 1.21", players = com.aetheris.android.data.model.PlayerCount(42, 200)),
    Server(id = "2", name = "valheim-survival", node = "Node-EU-2", status = ServerStatus.RUNNING, ip = "185.223.28.13", cpu = com.aetheris.android.data.model.ResourceUsage(22, 100, 22f), memory = com.aetheris.android.data.model.ResourceUsage(3072, 8192, 37.5f), disk = com.aetheris.android.data.model.ResourceUsage(8, 25, 32f), game = "Valheim", players = com.aetheris.android.data.model.PlayerCount(8, 10)),
    Server(id = "3", name = "garrys-mod-base", node = "Node-US-1", status = ServerStatus.RUNNING, ip = "23.105.130.44", cpu = com.aetheris.android.data.model.ResourceUsage(67, 100, 67f), memory = com.aetheris.android.data.model.ResourceUsage(8192, 16384, 50f), disk = com.aetheris.android.data.model.ResourceUsage(15, 40, 37.5f), game = "Garry's Mod", players = com.aetheris.android.data.model.PlayerCount(28, 64)),
    Server(id = "4", name = "rust-wipe-01", node = "Node-EU-3", status = ServerStatus.STOPPED, ip = "185.223.29.15", cpu = com.aetheris.android.data.model.ResourceUsage(0, 100, 0f), memory = com.aetheris.android.data.model.ResourceUsage(0, 8192, 0f), disk = com.aetheris.android.data.model.ResourceUsage(18, 35, 51.4f), game = "Rust"),
    Server(id = "5", name = "palworld-dedicated", node = "Node-US-2", status = ServerStatus.STARTING, ip = "23.105.131.22", cpu = com.aetheris.android.data.model.ResourceUsage(12, 100, 12f), memory = com.aetheris.android.data.model.ResourceUsage(2048, 32768, 6.25f), disk = com.aetheris.android.data.model.ResourceUsage(22, 60, 36.7f), game = "Palworld", players = com.aetheris.android.data.model.PlayerCount(0, 32)),
    Server(id = "6", name = "cs2-competitive", node = "Node-EU-1", status = ServerStatus.RUNNING, ip = "185.223.28.20", cpu = com.aetheris.android.data.model.ResourceUsage(55, 100, 55f), memory = com.aetheris.android.data.model.ResourceUsage(4096, 8192, 50f), disk = com.aetheris.android.data.model.ResourceUsage(12, 30, 40f), game = "CS2", players = com.aetheris.android.data.model.PlayerCount(9, 10)),
)
