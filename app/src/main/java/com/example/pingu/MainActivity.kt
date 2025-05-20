package com.example.pingu

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pingu.ui.theme.PinguTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppEntry()
        }
    }
}

@Composable
fun AppEntry() {
    PinguTheme {
        var cameraPermissionStatus by remember { mutableStateOf<PermissionStatus?>(null) }
        val context = LocalContext.current

        // rememberPermissionState will invoke onPermissionResult with the initial status
        // and after any permission request.
        val cameraPermissionState = rememberPermissionState(
            permission = Manifest.permission.CAMERA
        ) { status -> // This callback updates our local state
            cameraPermissionStatus = status
        }

        // LaunchedEffect to request permission if it's not initially granted.
        // This runs once when AppEntry is first composed or if cameraPermissionState changes.
        // We only want to auto-request if the status determined by rememberPermissionState is DENIED initially.
        // If it's GRANTED, the UI will update through the callback.
        // If it's PERMANENTLY_DENIED or SHOW_RATIONALE from a previous session,
        // we might not want to auto-request without user interaction.
        LaunchedEffect(cameraPermissionState.status) { // Re-run if the initial status from PermissionState changes
            if (cameraPermissionState.status == PermissionStatus.DENIED && cameraPermissionStatus == null) {
                // If the initial status is DENIED and we haven't set our local status yet,
                // trigger a request.
                // Note: The 'cameraPermissionStatus == null' check prevents re-requesting if the user
                // explicitly denies it and the status becomes DENIED again.
                cameraPermissionState.launchPermissionRequest()
            } else if (cameraPermissionStatus == null) {
                // If not DENIED initially (e.g. GRANTED, or other states from previous interaction)
                // and local status is still null, set it from cameraPermissionState.
                cameraPermissionStatus = cameraPermissionState.status
            }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (cameraPermissionStatus) {
                    PermissionStatus.GRANTED -> {
                        CameraPreviewScreen(modifier = Modifier.fillMaxSize())
                    }
                    PermissionStatus.DENIED,
                    PermissionStatus.SHOW_RATIONALE,
                    PermissionStatus.PERMANENTLY_DENIED -> {
                        GrantPermissionButton(
                            currentStatus = cameraPermissionStatus ?: PermissionStatus.DENIED, // Provide a default
                            onRequest = {
                                if (cameraPermissionStatus == PermissionStatus.PERMANENTLY_DENIED) {
                                    openAppSettings(context)
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            }
                        )
                    }
                    null -> { // Initializing or waiting for first status from rememberPermissionState
                        Text("Checking camera permission...")
                        // The LaunchedEffect above handles the transition from this state.
                    }
                }
            }
        }
    }
}

@Composable
fun GrantPermissionButton(currentStatus: PermissionStatus, onRequest: () -> Unit) {
    Button(
        onClick = onRequest,
        modifier = Modifier.size(width = 300.dp, height = 80.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = if (currentStatus == PermissionStatus.PERMANENTLY_DENIED) "OPEN SETTINGS" else "GRANT CAMERA PERMISSION",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    // Re-bind if lifecycleOwner or cameraController changes.
    // In this simple case, lifecycleOwner is the main trigger.
    LaunchedEffect(lifecycleOwner, cameraController) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.controller = cameraController
            }
        },
        modifier = modifier
    )
}

// --- Previews ---

@Preview(showBackground = true, name = "App - Camera Granted")
@Composable
fun AppEntryPreview_PermissionGranted() {
    PinguTheme {
        // Simulate AppEntry showing CameraPreviewScreen when permission is GRANTED
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CameraPreviewScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(showBackground = true, name = "App - Needs Permission (Denied)")
@Composable
fun AppEntryPreview_NeedsPermissionDenied() {
    PinguTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GrantPermissionButton(currentStatus = PermissionStatus.DENIED, onRequest = {})
        }
    }
}

@Preview(showBackground = true, name = "App - Needs Permission (Permanently Denied)")
@Composable
fun AppEntryPreview_NeedsPermissionPermanentlyDenied() {
    PinguTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GrantPermissionButton(currentStatus = PermissionStatus.PERMANENTLY_DENIED, onRequest = {})
        }
    }
}


@Preview(showBackground = true, name = "Direct Camera Preview")
@Composable
fun CameraPreviewScreenPreview() {
    PinguTheme {
        CameraPreviewScreen(modifier = Modifier.fillMaxSize())
    }
}