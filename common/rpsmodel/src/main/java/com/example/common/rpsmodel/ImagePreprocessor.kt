package com.example.common.rpsmodel

import android.graphics.Bitmap
// Import ExecuTorch Tensor
import org.pytorch.executorch.Tensor // ExecuTorch Tensor

object ImagePreprocessor {
    // CRITICAL: This MUST match the input_size used when exporting the model (e.g., 48)
    private const val MODEL_INPUT_WIDTH = 48 // Example: Use the COMPATIBLE_INPUT_SIZE
    private const val MODEL_INPUT_HEIGHT = 48 // Example: Use the COMPATIBLE_INPUT_SIZE

    // Normalization parameters should ideally match what was used during training
    // If your Python export didn't explicitly involve a different normalization
    // for the *exported model data itself* (unlikely for basic export),
    // these values for preprocessing the *live camera image* should still be standard.
    private const val MEAN = 0.5f // Or 0.0f if inputs were [0,1] and then normalized
    private const val STD  = 0.5f // Or 1.0f if inputs were [0,1] and then normalized, or 0.229, 0.224, 0.225 for ImageNet stds if applicable

    /** Resize→grayscale→normalize→[1×1×MODEL_INPUT_HEIGHT×MODEL_INPUT_WIDTH] Float32 tensor */
    fun bitmapToFloat32Tensor(bitmap: Bitmap): org.pytorch.executorch.Tensor {
        // 1. Resize the input bitmap to the size the model expects
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT, true)

        // 2. Convert to grayscale and normalize
        //    (Assuming the model was trained on grayscale images normalized to [-1, 1] or [0, 1])
        val numPixels = MODEL_INPUT_WIDTH * MODEL_INPUT_HEIGHT
        val floatBuffer = FloatArray(numPixels)
        val pixels = IntArray(numPixels)
        resizedBitmap.getPixels(pixels, 0, MODEL_INPUT_WIDTH, 0, 0, MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            // Extract RGB components (assuming ARGB_8888)
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            // Convert to grayscale (standard luminance calculation)
            val grayscale = (0.299f * r + 0.587f * g + 0.114f * b)

            // Normalize
            floatBuffer[i] = (grayscale - MEAN) / STD
        }

        // 3. Create the ExecuTorch Tensor
        //    Shape: [batch_size, channels, height, width]
        //    For your model: [1, 1, MODEL_INPUT_HEIGHT, MODEL_INPUT_WIDTH]
        return org.pytorch.executorch.Tensor.fromBlob(
            floatBuffer,
            longArrayOf(1L, 1L, MODEL_INPUT_HEIGHT.toLong(), MODEL_INPUT_WIDTH.toLong())
        )
    }
}