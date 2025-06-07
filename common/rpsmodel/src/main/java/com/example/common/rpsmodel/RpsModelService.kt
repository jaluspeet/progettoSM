package com.example.common.rpsmodel

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import com.example.common.game.RpsGame
import com.example.common.game.RpsMatch
import com.example.common.storage.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RpsModelService : Service() {
    private val binder = LocalBinder()
    private lateinit var classifier: RpsModelClassifier
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        classifier = RpsModelClassifier(this)
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    inner class LocalBinder : Binder() {
        fun getService(): RpsModelService = this@RpsModelService
    }

    /**
     * Asynchronously classifies [bitmap], saves the match to the repo,
     * and posts the result back on the Main thread via [callback].
     */
    fun classify(bitmap: Bitmap, callback: (match: RpsMatch) -> Unit) {
        serviceScope.launch {
            val choice = classifier.predict(bitmap) ?: return@launch
            val match = RpsGame.play(choice)
            ServiceLocator.provideMatchHistoryRepository().insertMatch(match)
            withContext(Dispatchers.Main) {
                callback(match)
            }
        }
    }
}