package com.aetheris.android.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetheris.android.ui.theme.AetherisAccent
import com.aetheris.android.ui.theme.AetherisBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var phase by remember { mutableIntStateOf(0) }

    // Phase 0: Logo appears (0-800ms)
    // Phase 1: Glow pulses (800-1600ms)
    // Phase 2: Text fades in (1600-2200ms)
    // Phase 3: Everything fades out (2200-2800ms)
    // Phase 4: Done (2800ms)

    LaunchedEffect(Unit) {
        delay(800)
        phase = 1
        delay(800)
        phase = 2
        delay(600)
        phase = 3
        delay(600)
        onSplashFinished()
    }

    // Logo scale animation
    val logoScale by animateFloatAsState(
        targetValue = when {
            phase >= 1 -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    // Logo alpha
    val logoAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 3 -> 0f
            else -> 1f
        },
        animationSpec = tween(400, easing = FastOutLinearInEasing),
        label = "logoAlpha"
    )

    // Glow pulse animation
    val glowAlpha by remember(phase) {
        derivedStateOf { phase }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Text alpha
    val textAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 2 && phase < 3 -> 1f
            phase >= 3 -> 0f
            else -> 0f
        },
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "textAlpha"
    )

    // Subtitle alpha
    val subtitleAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 2 && phase < 3 -> 0.7f
            phase >= 3 -> 0f
            else -> 0f
        },
        animationSpec = tween(500, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )

    // Background fade
    val backgroundAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 3 -> 0f
            else -> 1f
        },
        animationSpec = tween(500, easing = FastOutLinearInEasing),
        label = "backgroundAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(backgroundAlpha)
            .background(AetherisBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo "A" with glow effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow behind logo
                if (phase in 1..2) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .alpha(glowPulse * 0.4f)
                            .graphicsLayer {
                                scaleX = 1.5f
                                scaleY = 1.5f
                            }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AetherisAccent.copy(alpha = 0.6f),
                                        Color.Transparent
                                    ),
                                    radius = 80f
                                )
                            )
                    )
                }

                // The "A" logo
                Text(
                    text = "A",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = AetherisAccent,
                    modifier = Modifier.alpha(logoAlpha)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand name
            Text(
                text = "AETHERIS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 8.sp,
                color = Color.White.copy(alpha = textAlpha),
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Billing & Virtualization Platform",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = subtitleAlpha * 0.5f),
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }
    }
}
