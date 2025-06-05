package com.example.common.rpsmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.common.game.RpsChoice
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RpsModelClassifier(
    context: Context,
    assetName: String = "rpsmodel.pte"
) {
    private var module: Module? = null
    private val classes = listOf(RpsChoice.ROCK, RpsChoice.PAPER, RpsChoice.SCISSORS)

    init {
        try {
            val path = copyAsset(context, assetName)
            Log.d(TAG, "Loading ExecuTorch model from $path")
            module = Module.load(path)
            Log.d(TAG, "ExecuTorch Model loaded ✓")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load ExecuTorch model", e)
        }
    }

    fun predict(bitmap: Bitmap): RpsChoice? {
        val mdl = module ?: run {
            Log.e(TAG, "ExecuTorch Module not initialized")
            return null
        }
        return try {
            val inputTensor: org.pytorch.executorch.Tensor = ImagePreprocessor.bitmapToFloat32Tensor(bitmap)
            val inputEValue = EValue.from(inputTensor)
            val outputEValues: Array<EValue> = mdl.forward(inputEValue)

            if (outputEValues.isEmpty()) {
                Log.e(TAG, "Model output is empty")
                return null
            }

            val outputTensor: org.pytorch.executorch.Tensor = outputEValues[0].toTensor()
            val scores = outputTensor.dataAsFloatArray

            if (scores.isEmpty()) {
                Log.e(TAG, "Scores array is empty")
                return null
            }

            val idx = scores.indices.maxByOrNull { scores[it] } ?: -1
            classes.getOrNull(idx).also { Log.d(TAG, "predicted=$it") }
        } catch (e: Exception) {
            Log.e(TAG, "ExecuTorch Inference failed", e)
            null
        }
    }

    @Throws(IOException::class)
    private fun copyAsset(context: Context, name: String): String {
        val outFile = File(context.filesDir, name)

        context.assets.open(name).use { inp ->
            FileOutputStream(outFile).use { out ->
                inp.copyTo(out)
                Log.d(TAG, "Copied asset $name to ${outFile.absolutePath}")
            }
        }

        return outFile.absolutePath
    }

    companion object { private const val TAG = "RpsModelClassifier" }
}