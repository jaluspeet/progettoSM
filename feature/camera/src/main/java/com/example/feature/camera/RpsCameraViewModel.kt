package com.example.feature.camera

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.game.RpsMatch
import com.example.common.storage.ServiceLocator
import kotlinx.coroutines.launch

class RpsCameraViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = ServiceLocator.provideMatchHistoryRepository()

    private val _lastMatch = kotlinx.coroutines.flow.MutableStateFlow<RpsMatch?>(null)
    val lastMatch = _lastMatch as kotlinx.coroutines.flow.StateFlow<RpsMatch?>

    fun classifyAndSave(bitmap: Bitmap, classifier: (Bitmap) -> RpsMatch?) {
        viewModelScope.launch {
            val match = classifier(bitmap) ?: return@launch
            repository.insertMatch(match)
            _lastMatch.value = match
        }
    }

    fun handleMatch(match: RpsMatch) {
        _lastMatch.value = match
    }

    fun reset() {
        _lastMatch.value = null
    }
}