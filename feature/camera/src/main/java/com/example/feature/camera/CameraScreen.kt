package com.example.feature.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.ImageFormat
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.compose.LocalLifecycleOwner // Updated import
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.game.RpsChoice
import com.example.common.game.RpsMatch
import java.io.ByteArrayOutputStream

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: RpsCameraViewModel = viewModel(
        factory = AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val match by viewModel.lastMatch.collectAsState(initial = null) // Changed var to val

    // Set up the Camera controller
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            // imageCaptureEnabled = true // Corrected property access
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // PreviewView to show camera feed
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = cameraController
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Capture button
        Button(
            onClick = {
                cameraController.takePicture(
                    ContextCompat.getMainExecutor(context), // Used ContextCompat
                    object : ImageCapture.OnImageCapturedCallback() { // Corrected callback type
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = imageProxyToBitmap(image)
                            image.close()
                            bitmap?.let { viewModel.classifyAndSave(it) }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            // TODO: handle errors
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Text("Capture")
        }

        // Show result in a dialog
        match?.let { result ->
            AlertDialog(
                onDismissRequest = { viewModel.reset() },
                title = { Text("Result: ${result.result}") },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("You: ${result.playerChoice.toEmoji()}",
                            style = MaterialTheme.typography.headlineMedium)
                        Text("AI:  ${result.aiChoice.toEmoji()}",
                            style = MaterialTheme.typography.headlineMedium)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.reset() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuv = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val outStream = ByteArrayOutputStream().apply {
        yuv.compressToJpeg(Rect(0, 0, image.width, image.height), 100, this)
    }
    return BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size())
}

private fun RpsChoice.toEmoji(): String = when (this) {
    RpsChoice.ROCK -> "🪨"
    RpsChoice.PAPER -> "📄"
    RpsChoice.SCISSORS -> "✂️"
}