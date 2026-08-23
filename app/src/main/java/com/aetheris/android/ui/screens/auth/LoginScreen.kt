package com.aetheris.android.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aetheris.android.data.repository.AetherisRepository
import com.aetheris.android.ui.theme.AetherisColors
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    serverUrl: String,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Gradient background
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AetherisColors.TextSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AetherisColors.Accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Dns,
                    contentDescription = "Aetheris",
                    tint = AetherisColors.Accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sign in to Aetheris",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (serverUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = serverUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = AetherisColors.TextMuted
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = AetherisColors.Border,
                    focusedBorderColor = AetherisColors.Accent,
                    cursorColor = AetherisColors.Accent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = AetherisColors.TextMuted
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                // Demo login - accept any credentials
                                isLoading = false
                                onLoginSuccess()
                            }
                        }
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = AetherisColors.Border,
                    focusedBorderColor = AetherisColors.Accent,
                    cursorColor = AetherisColors.Accent
                )
            )

            // Error message
            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = AetherisColors.Error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot password
            TextButton(
                onClick = { /* TODO: password reset */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.bodySmall,
                    color = AetherisColors.Accent
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            isLoading = false
                            onLoginSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AetherisColors.Accent,
                    disabledContainerColor = AetherisColors.Accent.copy(alpha = 0.3f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = AetherisColors.Border
                )
                Text(
                    text = "  or continue with  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = AetherisColors.TextMuted
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = AetherisColors.Border
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social login buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google
                OutlinedButton(
                    onClick = { /* TODO: Google OAuth */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AetherisColors.Surface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bolt,
                        contentDescription = "Google",
                        tint = AetherisColors.TextSecondary
                    )
                }

                // Discord
                OutlinedButton(
                    onClick = { /* TODO: Discord OAuth */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AetherisColors.Surface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Forum,
                        contentDescription = "Discord",
                        tint = AetherisColors.TextSecondary
                    )
                }

                // Apple
                OutlinedButton(
                    onClick = { /* TODO: Apple OAuth */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AetherisColors.Surface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhoneIphone,
                        contentDescription = "Apple",
                        tint = AetherisColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Aetheris Project\nOpen source under AGPL-3.0",
                style = MaterialTheme.typography.bodySmall,
                color = AetherisColors.TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
