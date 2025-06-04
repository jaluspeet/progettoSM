package com.example.feature.camera

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.game.RpsGame
import com.example.common.game.RpsMatch
import com.example.common.rpsmodel.RpsModelClassifier
import com.example.common.storage.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RpsCameraViewModel(app: Application) : AndroidViewModel(app) {
    private val classifier = RpsModelClassifier(app)
    // no cast to PinguApplication, just pull from ServiceLocator
    private val repository = ServiceLocator.provideMatchHistoryRepository()

    private val _lastMatch = MutableStateFlow<RpsMatch?>(null)
    val lastMatch: StateFlow<RpsMatch?> = _lastMatch

    fun classifyAndSave(bitmap: Bitmap) {
        viewModelScope.launch {
            val playerChoice = classifier.predict(bitmap) ?: return@launch
            val match = RpsGame.play(playerChoice)
            repository.insertMatch(match)
            _lastMatch.value = match
        }
    }

    fun reset() {
        _lastMatch.value = null
    }
}