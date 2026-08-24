package com.aetheris.android.ui.screens.install

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheris.android.ui.theme.AetherisColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// ── Installation steps ─────────────────────────────────────────────

enum class InstallStep(
    val title: String,
    val description: String,
    val command: String
) {
    CONNECT(
        "Connecting to server",
        "Establishing SSH connection",
        "ssh root@{host}"
    ),
    CHECK_DOCKER(
        "Checking Docker",
        "Verifying Docker installation",
        "docker --version && docker compose version"
    ),
    INSTALL_DOCKER(
        "Installing Docker",
        "Installing Docker Engine and Compose",
        "curl -fsSL https://get.docker.com | sh"
    ),
    CLONE_REPO(
        "Cloning Aetheris",
        "Downloading Aetheris source code",
        "git clone https://github.com/aetheris-project/aetheris-app.git /opt/aetheris"
    ),
    CONFIGURE(
        "Configuring panel",
        "Generating environment configuration",
        "cd /opt/aetheris && cp .env.example .env"
    ),
    COMPOSE_UP(
        "Starting services",
        "Building and launching Docker containers",
        "cd /opt/aetheris && docker compose up -d --build"
    ),
    VERIFY(
        "Verifying installation",
        "Checking all services are healthy",
        "docker compose ps && curl -sf http://localhost:3000"
    ),
    COMPLETE(
        "Installation complete",
        "Aetheris panel is ready to use",
        ""
    )
}

// ── Data class ─────────────────────────────────────────────────────

data class InstallState(
    val currentStep: InstallStep? = null,
    val completedSteps: Set<InstallStep> = emptySet(),
    val failedStep: InstallStep? = null,
    val logs: List<String> = emptyList(),
    val isRunning: Boolean = false,
    val panelUrl: String = "",
    val host: String = "",
    val sshUser: String = "root",
    val sshPort: Int = 22
)

// ── Main screen ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallPanelScreen(
    onBack: () -> Unit,
    onComplete: (String) -> Unit
) {
    var state by remember { mutableStateOf(InstallState()) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Auto-scroll logs
    LaunchedEffect(state.logs.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Install Aetheris", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (state.isRunning) "Installing..." else "Remote panel setup",
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
                .padding(horizontal = 16.dp)
        ) {
            // Server connection form (shown before install starts)
            if (!state.isRunning && state.failedStep == null && state.currentStep == null) {
                ConnectionForm(
                    host = state.host,
                    sshUser = state.sshUser,
                    sshPort = state.sshPort,
                    onHostChange = { state = state.copy(host = it) },
                    onUserChange = { state = state.copy(sshUser = it) },
                    onPortChange = { state = state.copy(sshPort = it) },
                    onInstall = {
                        state = state.copy(isRunning = true)
                    }
                )
            }

            // Progress steps
            if (state.isRunning || state.completedSteps.isNotEmpty() || state.failedStep != null) {
                StepProgress(state = state)
            }

            // Logs console
            if (state.logs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LogConsole(
                    logs = state.logs,
                    scrollState = scrollState
                )
            }

            // Complete button
            if (state.currentStep == InstallStep.COMPLETE) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onComplete(state.panelUrl) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AetherisColors.Accent)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Open Panel", fontWeight = FontWeight.SemiBold)
                }
            }

            // Retry button
            if (state.failedStep != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { state = InstallState() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Retry")
                    }
                    Button(
                        onClick = { state = state.copy(isRunning = true, failedStep = null) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AetherisColors.Accent)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Continue")
                    }
                }
            }
        }
    }

    // Run installation in background
    if (state.isRunning && state.failedStep == null) {
        LaunchedEffect(state.isRunning) {
            runInstallation(
                host = state.host,
                user = state.sshUser,
                onStepStart = { step ->
                    state = state.copy(
                        currentStep = step,
                        logs = state.logs + "\n--- ${step.title} ---"
                    )
                },
                onLog = { line ->
                    state = state.copy(logs = state.logs + line)
                },
                onStepComplete = { step ->
                    state = state.copy(
                        completedSteps = state.completedSteps + step,
                        currentStep = null
                    )
                },
                onStepFailed = { step, error ->
                    state = state.copy(
                        failedStep = step,
                        isRunning = false,
                        currentStep = null,
                        logs = state.logs + "\nERROR: $error"
                    )
                },
                onComplete = { url ->
                    state = state.copy(
                        currentStep = InstallStep.COMPLETE,
                        isRunning = false,
                        panelUrl = url,
                        logs = state.logs + "\n=== Installation complete ===\nPanel URL: $url"
                    )
                }
            )
        }
    }
}

// ── Connection form ────────────────────────────────────────────────

@Composable
private fun ConnectionForm(
    host: String,
    sshUser: String,
    sshPort: Int,
    onHostChange: (String) -> Unit,
    onUserChange: (String) -> Unit,
    onPortChange: (Int) -> Unit,
    onInstall: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        // Info card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AetherisColors.Accent.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint = AetherisColors.Accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Remote Installation", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Install Aetheris on a remote server via SSH. " +
                        "The server must have SSH access and be running Linux (Ubuntu/Debian recommended).",
                        style = MaterialTheme.typography.bodySmall,
                        color = AetherisColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Server Connection", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        // Host
        OutlinedTextField(
            value = host,
            onValueChange = onHostChange,
            label = { Text("Server IP or hostname") },
            placeholder = { Text("192.168.1.100") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = AetherisColors.Border,
                focusedBorderColor = AetherisColors.Accent,
                cursorColor = AetherisColors.Accent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // User
            OutlinedTextField(
                value = sshUser,
                onValueChange = onUserChange,
                label = { Text("SSH User") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = AetherisColors.Border,
                    focusedBorderColor = AetherisColors.Accent,
                    cursorColor = AetherisColors.Accent
                )
            )

            // Port
            OutlinedTextField(
                value = sshPort.toString(),
                onValueChange = { onPortChange(it.toIntOrNull() ?: 22) },
                label = { Text("Port") },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = AetherisColors.Border,
                    focusedBorderColor = AetherisColors.Accent,
                    cursorColor = AetherisColors.Accent
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Requirements
        Text("Requirements", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val requirements = listOf(
            "Root or sudo access on the server",
            "Minimum 2GB RAM and 20GB disk",
            "Ports 80, 443, and 3000 available",
            "Internet access for downloading packages"
        )
        requirements.forEach { req ->
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = AetherisColors.Accent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(req, style = MaterialTheme.typography.bodySmall, color = AetherisColors.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Install button
        Button(
            onClick = onInstall,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = host.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AetherisColors.Accent,
                disabledContainerColor = AetherisColors.Accent.copy(alpha = 0.3f)
            )
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Start Installation", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ── Step progress ──────────────────────────────────────────────────

@Composable
private fun StepProgress(state: InstallState) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        InstallStep.entries.forEach { step ->
            val isCompleted = step in state.completedSteps
            val isCurrent = state.currentStep == step
            val isFailed = state.failedStep == step

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> AetherisColors.Success
                                isCurrent -> AetherisColors.Accent
                                isFailed -> AetherisColors.Error
                                else -> AetherisColors.SurfaceHighest
                            }.copy(alpha = if (isCompleted || isCurrent || isFailed) 0.2f else 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isCompleted -> Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = AetherisColors.Success,
                            modifier = Modifier.size(16.dp)
                        )
                        isCurrent -> CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = AetherisColors.Accent
                        )
                        isFailed -> Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = AetherisColors.Error,
                            modifier = Modifier.size(16.dp)
                        )
                        else -> Text(
                            "${step.ordinal + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AetherisColors.TextMuted
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        step.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent || isFailed) FontWeight.SemiBold else FontWeight.Normal,
                        color = when {
                            isFailed -> AetherisColors.Error
                            isCompleted -> AetherisColors.Success
                            isCurrent -> AetherisColors.TextPrimary
                            else -> AetherisColors.TextMuted
                        }
                    )
                    if (step.description.isNotEmpty() && (isCurrent || isFailed)) {
                        Text(
                            step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = AetherisColors.TextMuted
                        )
                    }
                }
            }
        }
    }
}

// ── Log console ────────────────────────────────────────────────────

@Composable
private fun LogConsole(
    logs: List<String>,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {
            Text(
                "Installation Log",
                style = MaterialTheme.typography.labelSmall,
                color = AetherisColors.TextMuted,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            logs.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    ),
                    color = when {
                        line.startsWith("ERROR") -> AetherisColors.Error
                        line.startsWith("=== ") -> AetherisColors.Accent
                        line.startsWith("---") -> AetherisColors.Info
                        line.contains("OK") || line.contains("done") || line.contains("success") -> AetherisColors.Success
                        line.contains("WARN") -> AetherisColors.Warning
                        else -> Color(0xFFA1A1AA)
                    }
                )
            }
        }
    }
}

// ── Installation runner (simulated for demo) ───────────────────────

private suspend fun runInstallation(
    host: String,
    user: String,
    onStepStart: (InstallStep) -> Unit,
    onLog: (String) -> Unit,
    onStepComplete: (InstallStep) -> Unit,
    onStepFailed: (InstallStep, String) -> Unit,
    onComplete: (String) -> Unit
) {
    val steps = InstallStep.entries.filter { it != InstallStep.COMPLETE }

    for (step in steps) {
        if (!kotlin.coroutines.coroutineContext.isActive) return
        onStepStart(step)
        onLog("[${java.time.LocalTime.now().toString().substring(0, 8)}] Starting: ${step.title}")

        // Simulate command execution with realistic delays
        when (step) {
            InstallStep.CONNECT -> {
                onLog("$ user@$host -p 22")
                delay(1500)
                onLog("Connected to $host")
                onLog("System: Ubuntu 24.04 LTS | Arch: x86_64")
            }
            InstallStep.CHECK_DOCKER -> {
                onLog("$ docker --version")
                delay(800)
                // Simulate Docker not found
                onLog("bash: docker: command not found")
                onLog("Docker not installed - will install...")
            }
            InstallStep.INSTALL_DOCKER -> {
                onLog("$ curl -fsSL https://get.docker.com | sh")
                delay(2000)
                onLog("Installing Docker Engine...")
                onLog("Checking package availability...")
                delay(1500)
                onLog("Reading package lists...")
                onLog("Building dependency tree...")
                delay(1000)
                onLog("Installing docker-ce docker-ce-cli containerd.io...")
                delay(3000)
                onLog("Adding current user to docker group...")
                delay(800)
                onLog("Docker Engine 28.3.3 installed")
                onLog("Docker Compose plugin v2.38.1 installed")
            }
            InstallStep.CLONE_REPO -> {
                onLog("$ git clone https://github.com/aetheris-project/aetheris-app.git /opt/aetheris")
                delay(2000)
                onLog("Cloning into '/opt/aetheris'...")
                onLog("Receiving objects: 100% (1247/1247), 4.82 MiB | 12.34 MiB/s")
                delay(1000)
                onLog("Resolving deltas: 100% (423/423), done.")
            }
            InstallStep.CONFIGURE -> {
                onLog("$ cd /opt/aetheris")
                onLog("$ cp .env.example .env")
                delay(500)
                onLog("Generating random secrets...")
                delay(800)
                onLog("JWT_SECRET=$(openssl rand -hex 32)")
                onLog("POSTGRES_PASSWORD=$(openssl rand -base64 24)")
                delay(500)
                onLog(".env configured successfully")
            }
            InstallStep.COMPOSE_UP -> {
                onLog("$ docker compose up -d --build")
                delay(2000)
                onLog("[+] Building 45.2s (24/24) FINISHED")
                onLog("[+] Running 8/8")
                delay(1000)
                onLog(" Container aetheris-postgres-1   Started")
                onLog(" Container aetheris-redis-1      Started")
                delay(1500)
                onLog(" Container aetheris-worker-1     Started")
                onLog(" Container aetheris-web-1        Started")
                delay(1000)
                onLog(" Container aetheris-nginx-1      Started")
                onLog("All 5 services started")
            }
            InstallStep.VERIFY -> {
                onLog("$ docker compose ps")
                delay(1000)
                onLog("NAME                  STATUS          PORTS")
                onLog("aetheris-web-1        Up 30s          0.0.0.0:3000->3000/tcp")
                onLog("aetheris-postgres-1   Up 30s          5432/tcp")
                onLog("aetheris-redis-1      Up 30s          6379/tcp")
                onLog("aetheris-worker-1     Up 30s")
                onLog("aetheris-nginx-1      Up 30s          0.0.0.0:80->80/tcp")
                delay(500)
                onLog("$ curl -sf http://localhost:3000")
                onLog("HTTP 200 - Aetheris panel responding")
            }
            InstallStep.COMPLETE -> {}
        }

        onLog("[${java.time.LocalTime.now().toString().substring(0, 8)}] Completed: ${step.title}")
        onStepComplete(step)
        delay(300)
    }

    onComplete("http://$host")
}
