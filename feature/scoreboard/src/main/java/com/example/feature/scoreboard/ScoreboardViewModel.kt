package com.example.feature.scoreboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.game.RpsMatch
import com.example.common.storage.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ScoreboardViewModel : ViewModel() {
    private val repository = ServiceLocator.provideMatchHistoryRepository()
    val matchHistory: StateFlow<List<RpsMatch>> = repository
        .allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}