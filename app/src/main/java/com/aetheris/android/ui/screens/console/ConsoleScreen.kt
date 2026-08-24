package com.aetheris.android.ui.screens.console

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.aetheris.android.ui.theme.AetherisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsoleScreen(
    serverId: String,
    onBack: () -> Unit
) {
    var isFullscreen by remember { mutableStateOf(false) }
    var showTypeSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Console", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Server: $serverId",
                                style = MaterialTheme.typography.bodySmall,
                                color = AetherisColors.TextMuted
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Console type selector
                        IconButton(onClick = { showTypeSelector = true }) {
                            Icon(Icons.Filled.Build, contentDescription = "Console type")
                        }
                        IconButton(onClick = { isFullscreen = !isFullscreen }) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Filled.Close else Icons.Filled.ArrowForward,
                                contentDescription = "Toggle fullscreen"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isFullscreen) Modifier else Modifier.padding(padding))
                .background(AetherisColors.Background)
        ) {
            // Console status bar
            if (!isFullscreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AetherisColors.Surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AetherisColors.Success)
                    )
                    Text(
                        text = "  Connected",
                        style = MaterialTheme.typography.labelMedium,
                        color = AetherisColors.Success
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Terminal",
                        style = MaterialTheme.typography.labelSmall,
                        color = AetherisColors.TextMuted
                    )
                }
            }

            // Terminal WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        // Load the console page
                        loadDataWithBaseURL(
                            null,
                            getTerminalHtml(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )

            // Command input
            if (!isFullscreen) {
                ConsoleInputBar()
            }
        }
    }

    if (showTypeSelector) {
        AlertDialog(
            onDismissRequest = { showTypeSelector = false },
            title = { Text("Console Type") },
            text = {
                Column {
                    ConsoleTypeOption("Terminal", "Server console and SSH", Icons.Filled.Build, onClick = { showTypeSelector = false })
                    Spacer(modifier = Modifier.height(8.dp))
                    ConsoleTypeOption("VNC", "Graphical remote desktop", Icons.Filled.Phone, onClick = { showTypeSelector = false })
                    Spacer(modifier = Modifier.height(8.dp))
                    ConsoleTypeOption("File Manager", "Browse server files", Icons.Filled.List, onClick = { showTypeSelector = false })
                }
            },
            confirmButton = {},
            containerColor = AetherisColors.Surface
        )
    }
}

@Composable
private fun ConsoleInputBar() {
    var command by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AetherisColors.Surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = AetherisColors.Accent,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        OutlinedTextField(
            value = command,
            onValueChange = { command = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Type a command...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AetherisColors.TextMuted
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = AetherisColors.Border,
                focusedBorderColor = AetherisColors.Accent,
                cursorColor = AetherisColors.Accent
            )
        )
        IconButton(
            onClick = {
                if (command.isNotBlank()) {
                    command = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Send",
                tint = AetherisColors.Accent
            )
        }
    }
}

@Composable
private fun ConsoleTypeOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = AetherisColors.SurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AetherisColors.Accent,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextMuted)
            }
        }
    }
}

private fun getTerminalHtml(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
        background: #09090B;
        color: #FAFAFA;
        font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', monospace;
        font-size: 13px;
        padding: 12px;
        height: 100vh;
        overflow-y: auto;
    }
    .line { margin-bottom: 2px; line-height: 1.5; }
    .prompt { color: #10B981; }
    .info { color: #3B82F6; }
    .warn { color: #F59E0B; }
    .error { color: #EF4444; }
    .dim { color: #71717A; }
    .success { color: #10B981; }
    .header {
        color: #A1A1AA;
        border-bottom: 1px solid #27272A;
        padding-bottom: 8px;
        margin-bottom: 8px;
    }
</style>
</head>
<body>
<div class="line header">Aetheris Server Console - Terminal</div>
<div class="line"><span class="info">[INFO]</span> Server minecraft-prod-01 is running</div>
<div class="line"><span class="info">[INFO]</span> Listening on 0.0.0.0:25565</div>
<div class="line"><span class="success">[OK]</span> Server started successfully</div>
<div class="line"><span class="dim">[16:42:01]</span> Player Steve joined the game</div>
<div class="line"><span class="dim">[16:42:15]</span> Player Steve: Hello!</div>
<div class="line"><span class="dim">[16:43:02]</span> Player Alex joined the game</div>
<div class="line"><span class="info">[INFO]</span> Loading world data...</div>
<div class="line"><span class="success">[OK]</span> World loaded in 3.2s</div>
<div class="line"><span class="warn">[WARN]</span> Memory usage at 62% (6144/16384 MB)</div>
<div class="line"><span class="dim">[16:45:30]</span> Auto-save complete</div>
<div class="line"><span class="info">[INFO]</span> TPS: 19.8 | Ping: 12ms</div>
<div class="line">&nbsp;</div>
<div class="line"><span class="prompt">$</span> <span class="dim">Waiting for input...</span></div>
</body>
</html>
""".trimIndent()
