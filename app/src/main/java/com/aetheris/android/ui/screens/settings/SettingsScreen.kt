package com.aetheris.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.local.PreferencesManager
import com.aetheris.android.ui.theme.AetherisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var darkMode by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }
    var lanDiscovery by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Configure your app", style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Connection
            SectionHeader("Connection")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Build,
                    title = "Server URL",
                    subtitle = "aetheris-panel.vercel.app",
                    trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = AetherisColors.TextMuted) }
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Refresh,
                    title = "LAN Discovery",
                    subtitle = "Auto-discover panels on local network",
                    trailing = {
                        Switch(
                            checked = lanDiscovery,
                            onCheckedChange = { lanDiscovery = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = AetherisColors.Accent)
                        )
                    }
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Refresh,
                    title = "Refresh Interval",
                    subtitle = "Every 30 seconds",
                    trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = AetherisColors.TextMuted) }
                )
            }

            // Appearance
            SectionHeader("Appearance")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Refresh,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    trailing = {
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = AetherisColors.Accent)
                        )
                    }
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Build,
                    title = "Language",
                    subtitle = "English",
                    trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = AetherisColors.TextMuted) }
                )
            }

            // Notifications
            SectionHeader("Notifications")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Notifications,
                    title = "Push Notifications",
                    subtitle = "Server alerts and status changes",
                    trailing = {
                        Switch(
                            checked = notifications,
                            onCheckedChange = { notifications = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = AetherisColors.Accent)
                        )
                    }
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Warning,
                    title = "Critical Alerts",
                    subtitle = "Server down, high CPU/memory",
                    trailing = {
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                            colors = SwitchDefaults.colors(checkedTrackColor = AetherisColors.Accent)
                        )
                    }
                )
            }

            // About
            SectionHeader("About")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Version",
                    subtitle = "1.0.0 (Build 1)"
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "License",
                    subtitle = "AGPL-3.0"
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Person,
                    title = "Contributing",
                    subtitle = "Pull requests welcome"
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Support",
                    subtitle = "discord.gg/6GcfebuT2A"
                )
                HorizontalDivider(color = AetherisColors.BorderSubtle)
                SettingsItem(
                    icon = Icons.Filled.Home,
                    title = "Website",
                    subtitle = "aetheris-web.vercel.app"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout button
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AetherisColors.Error)
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out")
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out? You will need to enter your credentials again.") },
            confirmButton = {
                TextButton(
                    onClick = { showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.textButtonColors(contentColor = AetherisColors.Error)
                ) { Text("Sign Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
            containerColor = AetherisColors.Surface
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = AetherisColors.TextMuted,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AetherisColors.Surface)
    ) {
        Column(modifier = Modifier.padding(4.dp), content = content)
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AetherisColors.Accent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AetherisColors.Accent,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
        }
        trailing?.invoke()
    }
}
