package com.example.common.rpsmodel

import android.content.Context
import android.graphics.Bitmap
import com.example.common.game.RpsChoice
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RpsModelClassifier(context: Context, modelName: String = "rpsmodel.ptl") {

    private var module: Module? = null
    private val outputClasses = listOf(RpsChoice.ROCK, RpsChoice.PAPER, RpsChoice.SCISSORS)

    init {
        try {
            module = LiteModuleLoader.load(assetFilePath(context, modelName))
        } catch (e: IOException) {
            throw RuntimeException("Error loading PyTorch model: ${e.message}", e)
        }
    }

    @Throws(IOException::class)
    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file.absolutePath
    }

    fun predict(bitmap: Bitmap): RpsChoice? {
        if (module == null) {
            return null
        }

        try {
            val inputTensor = ImagePreprocessor.bitmapToGrayscaleTensor(bitmap)
            val outputTensor = module?.forward(IValue.from(inputTensor))?.toTensor()

            if (outputTensor == null) {
                return null
            }

            val scores = outputTensor.dataAsFloatArray
            var maxScoreIdx = -1
            var maxScore = -Float.MAX_VALUE

            for (i in scores.indices) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i]
                    maxScoreIdx = i
                }
            }

            return if (maxScoreIdx != -1 && maxScoreIdx < outputClasses.size) {
                outputClasses[maxScoreIdx]
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }
}