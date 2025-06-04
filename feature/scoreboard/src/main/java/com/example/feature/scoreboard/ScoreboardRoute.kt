package com.example.feature.scoreboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Route/composable entry point for the Scoreboard feature.
 * Collects the matchHistory StateFlow from the ViewModel and passes it to ScoreboardScreen.
 */
@Composable
fun ScoreboardRoute(
    viewModel: ScoreboardViewModel = viewModel()
) {
    val history by viewModel.matchHistory.collectAsState()
    ScoreboardScreen(matchHistory = history)
}