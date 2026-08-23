package com.aetheris.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aetheris.android.data.local.PreferencesManager
import com.aetheris.android.navigation.AetherisNavGraph
import com.aetheris.android.ui.theme.AetherisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by preferencesManager.darkMode.collectAsState(initial = true)
            val navController = rememberNavController()
            var isAuthenticated by remember { mutableStateOf(false) }

            // Check auth state on first composition
            LaunchedEffect(Unit) {
                isAuthenticated = preferencesManager.isAuthenticated()
            }

            AetherisTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AetherisNavGraph(
                        navController = navController,
                        isAuthenticated = isAuthenticated
                    )
                }
            }
        }
    }
}
