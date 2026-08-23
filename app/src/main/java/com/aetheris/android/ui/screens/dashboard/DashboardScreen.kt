package com.aetheris.android.ui.screens.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.model.*
import com.aetheris.android.navigation.Screen
import com.aetheris.android.ui.theme.AetherisColors
import java.text.NumberFormat
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {
    var stats by remember { mutableStateOf(
        DashboardStats(
            activeServers = 47,
            totalNodes = 12,
            monthlyRevenue = 8942.50,
            totalClients = 312,
            outstanding = 1280.00,
            uptime = "99.97%"
        )
    ) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Welcome back",
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Stats grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Active Servers",
                        value = "${stats.activeServers}",
                        icon = Icons.Filled.Dns,
                        color = AetherisColors.Success,
                        trend = "+3"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Nodes",
                        value = "${stats.totalNodes}",
                        icon = Icons.Filled.Storage,
                        color = AetherisColors.Info,
                        trend = "+1"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Revenue",
                        value = formatCurrency(stats.monthlyRevenue),
                        icon = Icons.Filled.AttachMoney,
                        color = AetherisColors.Accent,
                        trend = "+18.4%"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Clients",
                        value = "${stats.totalClients}",
                        icon = Icons.Filled.People,
                        color = AetherisColors.Warning,
                        trend = "+24"
                    )
                }
            }

            // Uptime card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AetherisColors.Surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            AetherisColors.Success,
                                            AetherisColors.Accent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = AetherisColors.Success,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = "Platform Uptime",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Last 30 days",
                                style = MaterialTheme.typography.bodySmall,
                                color = AetherisColors.TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = stats.uptime,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AetherisColors.Success
                        )
                    }
                }
            }

            // Outstanding balance
            if (stats.outstanding > 0) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AetherisColors.Warning.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = AetherisColors.Warning,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = "Outstanding Balance",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${stats.totalClients} clients with unpaid invoices",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AetherisColors.TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = formatCurrency(stats.outstanding),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AetherisColors.Warning
                            )
                        }
                    }
                }
            }

            // Quick actions
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionChip(
                        label = "Servers",
                        icon = Icons.Filled.Dns,
                        onClick = { onNavigate(Screen.Servers.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionChip(
                        label = "Billing",
                        icon = Icons.Filled.Receipt,
                        onClick = { onNavigate(Screen.Billing.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionChip(
                        label = "Alerts",
                        icon = Icons.Filled.Notifications,
                        onClick = { onNavigate(Screen.Alerts.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionChip(
                        label = "Settings",
                        icon = Icons.Filled.Settings,
                        onClick = { onNavigate(Screen.Settings.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // System health
            item {
                Text(
                    text = "System Health",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            items(getSystemHealth()) { item ->
                HealthItem(item = item)
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    trend: String = ""
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AetherisColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (trend.isNotEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = trend,
                        style = MaterialTheme.typography.labelSmall,
                        color = AetherisColors.Success,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = AetherisColors.TextMuted
            )
        }
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = AetherisColors.Surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AetherisColors.Accent,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun HealthItem(item: SystemHealthItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AetherisColors.Surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(item.statusColor)
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = item.status,
            style = MaterialTheme.typography.bodySmall,
            color = item.statusColor
        )
        if (item.latency.isNotEmpty()) {
            Text(
                text = "  ${item.latency}",
                style = MaterialTheme.typography.bodySmall,
                color = AetherisColors.TextMuted
            )
        }
    }
}

private data class SystemHealthItem(
    val name: String,
    val status: String,
    val statusColor: androidx.compose.ui.graphics.Color,
    val latency: String = ""
)

private fun getSystemHealth() = listOf(
    SystemHealthItem("PostgreSQL", "Operational", AetherisColors.Success, "12ms"),
    SystemHealthItem("Redis", "Operational", AetherisColors.Success, "2ms"),
    SystemHealthItem("BullMQ Workers", "Operational", AetherisColors.Success, "1ms"),
    SystemHealthItem("Nginx", "Operational", AetherisColors.Success, "18ms"),
    SystemHealthItem("Docker", "Operational", AetherisColors.Success),
)

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance("USD")
    }
    return formatter.format(amount)
}

private fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.clip(RoundedCornerShape(12.dp))
    )
}
