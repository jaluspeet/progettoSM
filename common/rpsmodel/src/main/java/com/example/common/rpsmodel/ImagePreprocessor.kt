package com.example.common.rpsmodel

import android.graphics.Bitmap
import androidx.core.graphics.scale
import org.pytorch.executorch.Tensor

object ImagePreprocessor {
    private const val MODEL_INPUT_WIDTH = 48
    private const val MODEL_INPUT_HEIGHT = 48
    private const val MEAN = 0.5f
    private const val STD  = 0.5f

    fun bitmapToFloat32Tensor(bitmap: Bitmap): Tensor {
        val resizedBitmap = bitmap.scale(MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT)
        val numPixels = MODEL_INPUT_WIDTH * MODEL_INPUT_HEIGHT
        val floatBuffer = FloatArray(numPixels)
        val pixels = IntArray(numPixels)
        resizedBitmap.getPixels(pixels, 0, MODEL_INPUT_WIDTH, 0, 0, MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            val grayscale = (0.299f * r + 0.587f * g + 0.114f * b)

            floatBuffer[i] = (grayscale - MEAN) / STD
        }

        return Tensor.fromBlob(
            floatBuffer,
            longArrayOf(1L, 1L, MODEL_INPUT_HEIGHT.toLong(), MODEL_INPUT_WIDTH.toLong())
        )
    }
}