package com.aetheris.android.ui.screens.billing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.model.Invoice
import com.aetheris.android.data.model.InvoiceItem
import com.aetheris.android.data.model.InvoiceStatus
import com.aetheris.android.ui.theme.AetherisColors
import java.text.NumberFormat
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: String,
    onBack: () -> Unit
) {
    val invoice = remember {
        Invoice(
            id = invoiceId,
            number = "INV-2026-0087",
            status = InvoiceStatus.PENDING,
            amount = 247.50,
            description = "Minecraft Pro Server - Monthly",
            createdAt = "Aug 1, 2026",
            dueDate = "Sep 1, 2026",
            items = listOf(
                InvoiceItem("Minecraft Pro Server (16GB RAM, 4 vCPU)", 1, 199.00, 199.00),
                InvoiceItem("Premium DDoS Protection", 1, 25.00, 25.00),
                InvoiceItem("Daily Backups (30-day retention)", 1, 15.00, 15.00),
                InvoiceItem("Game Eggs Pack Addon", 1, 8.50, 8.50)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(invoice.number, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
            // Status and amount
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(invoice.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(shape = RoundedCornerShape(8.dp), color = getStatusColor(invoice.status).copy(alpha = 0.15f)) {
                            Text(invoice.status.name, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = getStatusColor(invoice.status), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(formatCurrency(invoice.amount), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Created: ${invoice.createdAt}", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Due: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Line items
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Line Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    invoice.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                                if (item.quantity > 1) {
                                    Text("${item.quantity} x ${formatCurrency(item.unitPrice)}", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
                                }
                            }
                            Text(formatCurrency(item.total), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        HorizontalDivider(color = AetherisColors.BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(formatCurrency(invoice.amount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AetherisColors.Accent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pay button (if pending)
            if (invoice.status == InvoiceStatus.PENDING) {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AetherisColors.Accent)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pay Now", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun getStatusColor(status: InvoiceStatus) = when (status) {
    InvoiceStatus.PAID -> AetherisColors.Success
    InvoiceStatus.PENDING -> AetherisColors.Warning
    InvoiceStatus.OVERDUE -> AetherisColors.Error
    InvoiceStatus.DRAFT -> AetherisColors.TextMuted
    InvoiceStatus.CANCELLED -> AetherisColors.TextMuted
    InvoiceStatus.REFUNDED -> AetherisColors.Info
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance().apply { currency = Currency.getInstance("USD") }
    return formatter.format(amount)
}
