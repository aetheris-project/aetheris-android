package com.aetheris.android.ui.screens.alerts

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.model.Alert
import com.aetheris.android.data.model.AlertSeverity
import com.aetheris.android.data.model.AlertType
import com.aetheris.android.ui.theme.AetherisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Critical", "Warning", "Info")

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Alerts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("3 unacknowledged", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AetherisColors.Accent.copy(alpha = 0.15f),
                            selectedLabelColor = AetherisColors.Accent
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                val alerts = getDemoAlerts().filter {
                    selectedFilter == "All" || it.severity.name.equals(selectedFilter, ignoreCase = true)
                }

                items(alerts) { alert ->
                    AlertCard(alert = alert)
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert) {
    var acknowledged by remember { mutableStateOf(alert.acknowledged) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (acknowledged) AetherisColors.Surface else AetherisColors.Surface.copy(alpha = 1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Severity icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(getSeverityColor(alert.severity).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getAlertIcon(alert.type),
                    contentDescription = null,
                    tint = getSeverityColor(alert.severity),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (acknowledged) AetherisColors.TextSecondary else MaterialTheme.colorScheme.onSurface
                    )
                    if (!acknowledged) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AetherisColors.Error)
                        )
                    }
                }
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = AetherisColors.TextMuted
                )
                Text(
                    text = alert.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = AetherisColors.TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!acknowledged) {
                IconButton(
                    onClick = { acknowledged = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Acknowledge",
                        tint = AetherisColors.Accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun getSeverityColor(severity: AlertSeverity) = when (severity) {
    AlertSeverity.CRITICAL -> AetherisColors.Error
    AlertSeverity.WARNING -> AetherisColors.Warning
    AlertSeverity.INFO -> AetherisColors.Info
}

private fun getAlertIcon(type: AlertType) = when (type) {
    AlertType.SERVER_DOWN -> Icons.Filled.Error
    AlertType.HIGH_CPU -> Icons.Filled.Star
    AlertType.HIGH_MEMORY -> Icons.Filled.Warning
    AlertType.DISK_FULL -> Icons.Filled.Folder
    AlertType.PAYMENT_FAILED -> Icons.Filled.Cancel
    AlertType.INVOICE_OVERDUE -> Icons.Filled.Description
    AlertType.NODE_OFFLINE -> Icons.Filled.Cloud
    AlertType.BACKUP_FAILED -> Icons.Filled.Save
}

private fun getDemoAlerts() = listOf(
    Alert(id = "1", type = AlertType.HIGH_CPU, severity = AlertSeverity.WARNING, title = "High CPU Usage", message = "cs2-competitive CPU at 92% for 15 minutes", timestamp = "5 min ago", serverId = "6"),
    Alert(id = "2", type = AlertType.INVOICE_OVERDUE, severity = AlertSeverity.CRITICAL, title = "Invoice Overdue", message = "INV-2026-0084 is 84 days overdue ($247.50)", timestamp = "1 hour ago"),
    Alert(id = "3", type = AlertType.HIGH_MEMORY, severity = AlertSeverity.WARNING, title = "Memory Pressure", message = "minecraft-prod-01 memory at 87% (14259/16384 MB)", timestamp = "2 hours ago", serverId = "1"),
    Alert(id = "4", type = AlertType.BACKUP_FAILED, severity = AlertSeverity.INFO, title = "Backup Completed", message = "Daily backup for minecraft-prod-01 completed (2.1 GB)", timestamp = "6 hours ago", serverId = "1"),
    Alert(id = "5", type = AlertType.NODE_OFFLINE, severity = AlertSeverity.CRITICAL, title = "Node Maintenance", message = "Node-US-2 scheduled maintenance starting tomorrow", timestamp = "12 hours ago"),
    Alert(id = "6", type = AlertType.PAYMENT_FAILED, severity = AlertSeverity.WARNING, title = "Payment Processing", message = "Invoice INV-2026-0087 payment retry scheduled for tomorrow", timestamp = "1 day ago"),
)
