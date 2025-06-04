package com.example.common.rpsmodel

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

object ImagePreprocessor {

    private const val MODEL_INPUT_WIDTH = 50
    private const val MODEL_INPUT_HEIGHT = 50

    private val NORM_MEAN_GS = floatArrayOf(0.5f)
    private val NORM_STD_GS = floatArrayOf(0.5f)


    fun bitmapToGrayscaleTensor(bitmap: Bitmap): Tensor {
        val resizedBitmap = bitmap.scale(MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT)

        val grayscaleBitmap = createBitmap(MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT)
        val canvas = android.graphics.Canvas(grayscaleBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(resizedBitmap, 0f, 0f, paint)

        val floatBuffer = Tensor.allocateFloatBuffer(1 * MODEL_INPUT_WIDTH * MODEL_INPUT_HEIGHT)
        val pixels = IntArray(MODEL_INPUT_WIDTH * MODEL_INPUT_HEIGHT)
        grayscaleBitmap.getPixels(pixels, 0, MODEL_INPUT_WIDTH, 0, 0, MODEL_INPUT_WIDTH, MODEL_INPUT_HEIGHT)

        for (i in 0 until (MODEL_INPUT_WIDTH * MODEL_INPUT_HEIGHT)) {
            val pixel = pixels[i]
            val grayValue = ((pixel shr 16) and 0xFF) / 255.0f
            val normalizedValue = (grayValue - NORM_MEAN_GS[0]) / NORM_STD_GS[0]
            floatBuffer.put(normalizedValue)
        }

        return Tensor.fromBlob(floatBuffer, longArrayOf(1, 1, MODEL_INPUT_HEIGHT.toLong(), MODEL_INPUT_WIDTH.toLong()))
    }
}