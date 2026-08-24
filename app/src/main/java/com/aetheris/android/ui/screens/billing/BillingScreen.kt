package com.aetheris.android.ui.screens.billing

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.model.*
import com.aetheris.android.ui.theme.AetherisColors
import java.text.NumberFormat
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    onInvoiceClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Invoices", "Services")

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Billing", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Manage invoices and services", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
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
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AetherisColors.Surface,
                contentColor = AetherisColors.Accent,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AetherisColors.Accent
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> BillingOverview(onInvoiceClick)
                1 -> InvoiceList(onInvoiceClick)
                2 -> ServiceList()
            }
        }
    }
}

@Composable
private fun BillingOverview(onInvoiceClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Balance summary
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AetherisColors.Accent)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Current Balance", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Text(formatCurrency(247.50), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Due by Sep 1, 2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                }
            }
        }

        // Stats row
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BillingStatCard("Paid This Month", formatCurrency(892.00), AetherisColors.Success, Modifier.weight(1f))
                BillingStatCard("Pending", formatCurrency(247.50), AetherisColors.Warning, Modifier.weight(1f))
            }
        }

        // Recent invoices
        item {
            Text("Recent Invoices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        }

        items(getDemoInvoices()) { invoice ->
            InvoiceRow(invoice = invoice, onClick = { onInvoiceClick(invoice.id) })
        }

        // Active services
        item {
            Text("Active Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        }

        items(getDemoServices()) { service ->
            ServiceRow(service = service)
        }
    }
}

@Composable
private fun InvoiceList(onInvoiceClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(getDemoInvoices()) { invoice ->
            InvoiceRow(invoice = invoice, onClick = { onInvoiceClick(invoice.id) })
        }
    }
}

@Composable
private fun ServiceList() {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(getDemoServices()) { service ->
            ServiceRow(service = service)
        }
    }
}

@Composable
private fun BillingStatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun InvoiceRow(invoice: Invoice, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(getInvoiceStatusColor(invoice.status).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getInvoiceIcon(invoice.status),
                    contentDescription = null,
                    tint = getInvoiceStatusColor(invoice.status),
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(invoice.number, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(invoice.description.ifEmpty { invoice.createdAt }, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatCurrency(invoice.amount), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(shape = RoundedCornerShape(4.dp), color = getInvoiceStatusColor(invoice.status).copy(alpha = 0.15f)) {
                    Text(invoice.status.name, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = getInvoiceStatusColor(invoice.status))
                }
            }
        }
    }
}

@Composable
private fun ServiceRow(service: Service) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(AetherisColors.Accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getServiceIcon(service.type),
                    contentDescription = null,
                    tint = AetherisColors.Accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(service.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(service.type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatCurrency(service.price), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(service.billingCycle, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
            }
        }
    }
}

private fun getInvoiceStatusColor(status: InvoiceStatus) = when (status) {
    InvoiceStatus.PAID -> AetherisColors.Success
    InvoiceStatus.PENDING -> AetherisColors.Warning
    InvoiceStatus.OVERDUE -> AetherisColors.Error
    InvoiceStatus.DRAFT -> AetherisColors.TextMuted
    InvoiceStatus.CANCELLED -> AetherisColors.TextMuted
    InvoiceStatus.REFUNDED -> AetherisColors.Info
}

private fun getInvoiceIcon(status: InvoiceStatus) = when (status) {
    InvoiceStatus.PAID -> Icons.Filled.CheckCircle
    InvoiceStatus.PENDING -> Icons.Outlined.DateRange
    InvoiceStatus.OVERDUE -> Icons.Filled.Warning
    InvoiceStatus.DRAFT -> Icons.Outlined.Edit
    InvoiceStatus.CANCELLED -> Icons.Filled.Cancel
    InvoiceStatus.REFUNDED -> Icons.Filled.Refresh
}

private fun getServiceIcon(type: ServiceType) = when (type) {
    ServiceType.GAME_SERVER -> Icons.Filled.Folder
    ServiceType.VPS -> Icons.Filled.Folder
    ServiceType.DEDICATED -> Icons.Filled.Lock
    ServiceType.WEB_HOSTING -> Icons.Filled.Public
    ServiceType.DOMAIN -> Icons.Filled.Link
    ServiceType.ADDON -> Icons.Filled.Star
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance().apply { currency = Currency.getInstance("USD") }
    return formatter.format(amount)
}

private fun getDemoInvoices() = listOf(
    Invoice(id = "1", number = "INV-2026-0087", status = InvoiceStatus.PENDING, amount = 247.50, description = "Minecraft Pro - Monthly", createdAt = "Aug 1, 2026", dueDate = "Sep 1, 2026"),
    Invoice(id = "2", number = "INV-2026-0086", status = InvoiceStatus.PAID, amount = 49.99, description = "Game Eggs Addon - Monthly", createdAt = "Jul 1, 2026", paidAt = "Jul 3, 2026"),
    Invoice(id = "3", number = "INV-2026-0085", status = InvoiceStatus.PAID, amount = 189.00, description = "VPS Node Upgrade", createdAt = "Jun 15, 2026", paidAt = "Jun 16, 2026"),
    Invoice(id = "4", number = "INV-2026-0084", status = InvoiceStatus.OVERDUE, amount = 247.50, description = "Minecraft Pro - Monthly", createdAt = "May 1, 2026"),
)

private fun getDemoServices() = listOf(
    Service(id = "1", name = "Minecraft Pro Server", type = ServiceType.GAME_SERVER, status = ServiceStatus.ACTIVE, price = 247.50, nextBillingDate = "Sep 1, 2026"),
    Service(id = "2", name = "Game Eggs Pack", type = ServiceType.ADDON, status = ServiceStatus.ACTIVE, price = 49.99, nextBillingDate = "Sep 1, 2026"),
    Service(id = "3", name = "EU Cloud VPS", type = ServiceType.VPS, status = ServiceStatus.ACTIVE, price = 89.00, nextBillingDate = "Sep 15, 2026"),
)
