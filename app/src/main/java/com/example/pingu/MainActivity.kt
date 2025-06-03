package com.example.pingu

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.game.RpsMatch
import com.example.common.game.RpsChoice
import com.example.common.game.RpsResult
import com.example.common.permission.PermissionStatus
import com.example.common.permission.openAppSettings
import com.example.common.permission.rememberPermissionState
import com.example.feature.camera.CameraScreen
import com.example.feature.scoreboard.ScoreboardScreen
import com.example.feature.settings.SettingsScreen
import com.example.pingu.ui.theme.PinguTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Camera : Screen("camera", "Camera", Icons.Filled.ThumbUp)
    object Scoreboard : Screen("scoreboard", "Scoreboard", Icons.Filled.Star)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    Screen.Camera,
    Screen.Scoreboard,
    Screen.Settings
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainAppStructure()
        }
    }
}

@Composable
fun MainAppStructure() {
    PinguTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Camera) }
        val context = LocalContext.current
        val cameraPermissionHandler = rememberPermissionState(
            permission = Manifest.permission.CAMERA
        )

        LaunchedEffect(cameraPermissionHandler.status, currentScreen) {
            if (currentScreen == Screen.Camera && cameraPermissionHandler.status == PermissionStatus.DENIED) {
                cameraPermissionHandler.launchPermissionRequest()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentScreen.route == screen.route,
                            onClick = { currentScreen = screen }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (currentScreen) {
                    Screen.Camera -> {
                        when (cameraPermissionHandler.status) {
                            PermissionStatus.GRANTED -> {
                                CameraScreen(modifier = Modifier.fillMaxSize())
                            }
                            PermissionStatus.DENIED,
                            PermissionStatus.SHOW_RATIONALE,
                            PermissionStatus.PERMANENTLY_DENIED -> {
                                PermissionRequestUI(
                                    status = cameraPermissionHandler.status,
                                    onRequestPermission = {
                                        if (cameraPermissionHandler.status == PermissionStatus.PERMANENTLY_DENIED) {
                                            openAppSettings(context)
                                        } else {
                                            cameraPermissionHandler.launchPermissionRequest()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Screen.Scoreboard -> {
                        // Dummy match history for now
                        val dummyMatches = listOf(
                            RpsMatch(RpsChoice.ROCK, RpsChoice.SCISSORS, RpsResult.WIN),
                            RpsMatch(RpsChoice.PAPER, RpsChoice.ROCK, RpsResult.WIN),
                            RpsMatch(RpsChoice.SCISSORS, RpsChoice.SCISSORS, RpsResult.DRAW)
                        )
                        ScoreboardScreen(
                            modifier = Modifier.fillMaxSize(),
                            matchHistory = dummyMatches
                        )
                    }

                    Screen.Settings -> {
                        SettingsScreen(
                            modifier = Modifier.fillMaxSize(),
                            onNavigateToAppSettings = { openAppSettings(context) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequestUI(
    status: PermissionStatus,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How are you expecting to play without the camera? I swear we aren't spying on you!",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.size(width = 300.dp, height = 70.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp, pressedElevation = 8.dp)
        ) {
            Text(
                text = if (status == PermissionStatus.PERMANENTLY_DENIED) "OPEN SETTINGS" else "GRANT CAMERA PERMISSION",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, name = "Main - Settings Selected")
@Composable
fun MainAppStructurePreview_Settings() {
    PinguTheme {
        val context = LocalContext.current
        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = screen.route == Screen.Settings.route,
                            onClick = { }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SettingsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavigateToAppSettings = { openAppSettings(context) }
                )
            }
        }
    }
}
