package com.aetheris.android

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.aetheris.android.data.local.PreferencesManager
import com.aetheris.android.navigation.AetherisNavGraph
import com.aetheris.android.ui.splash.SplashScreen
import com.aetheris.android.ui.theme.AetherisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12+ SplashScreen (shows while app loads)
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the system splash on screen while we load
        var keepSplash by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplash }

        // Fade out the system splash when ready
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f).apply {
                interpolator = DecelerateInterpolator()
                duration = 400L
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }

        setContent {
            val isDarkMode by preferencesManager.darkMode.collectAsState(initial = true)
            val navController = rememberNavController()
            var isAuthenticated by remember { mutableStateOf(false) }
            var showSplash by remember { mutableStateOf(true) }

            // Check auth state
            LaunchedEffect(Unit) {
                isAuthenticated = preferencesManager.isAuthenticated()
                // Small delay to let splash animation play
                delay(3200)
                keepSplash = false
            }

            AetherisTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen(
                            onSplashFinished = { showSplash = false }
                        )
                    } else {
                        AetherisNavGraph(
                            navController = navController,
                            isAuthenticated = isAuthenticated
                        )
                    }
                }
            }
        }
    }
}
