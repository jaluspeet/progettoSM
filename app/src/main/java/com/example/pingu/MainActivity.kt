package com.example.pingu

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        val context = LocalContext.current
        val cameraPermissionHandler = rememberPermissionState(
            permission = Manifest.permission.CAMERA
        )

        LaunchedEffect(cameraPermissionHandler.status) {
            if (cameraPermissionHandler.status == PermissionStatus.DENIED) {
                cameraPermissionHandler.launchPermissionRequest()
            }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (cameraPermissionHandler.status) {
                    PermissionStatus.GRANTED -> {
                        CameraPreviewScreen(modifier = Modifier.fillMaxSize())
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
        }
    }
}

/**
 * A composable that displays UI for requesting permission.
 * It includes an explanation text and a button to grant permission or open settings.
 */
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

@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

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

@Preview(showBackground = true, name = "App - Camera Granted")
@Composable
fun AppEntryPreview_PermissionGranted() {
    PinguTheme {
        CameraPreviewScreen(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, name = "App - Needs Permission (Denied)")
@Composable
fun AppEntryPreview_NeedsPermissionDenied() {
    PinguTheme {
        PermissionRequestUI(status = PermissionStatus.DENIED, onRequestPermission = {})
    }
}

@Preview(showBackground = true, name = "App - Needs Permission (Permanently Denied)")
@Composable
fun AppEntryPreview_NeedsPermissionPermanentlyDenied() {
    PinguTheme {
        PermissionRequestUI(status = PermissionStatus.PERMANENTLY_DENIED, onRequestPermission = {})
    }
}

@Preview(showBackground = true, name = "App - Needs Permission (Show Rationale)")
@Composable
fun AppEntryPreview_NeedsPermissionShowRationale() {
    PinguTheme {
        PermissionRequestUI(status = PermissionStatus.SHOW_RATIONALE, onRequestPermission = {})
    }
}


@Preview(showBackground = true, name = "Direct Camera Preview")
@Composable
fun CameraPreviewScreenPreview() {
    PinguTheme {
        CameraPreviewScreen(modifier = Modifier.fillMaxSize())
    }
}