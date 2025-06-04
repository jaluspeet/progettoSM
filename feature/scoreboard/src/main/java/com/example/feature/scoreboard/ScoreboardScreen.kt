package com.example.feature.scoreboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common.game.RpsChoice
import com.example.common.game.RpsMatch
import com.example.common.game.RpsResult

/**
 * Displays the list of past rock-paper-scissors matches.
 *
 * @param matchHistory the list of matches to display, most recent first.
 * @param modifier optional Compose modifier.
 */
@Composable
fun ScoreboardScreen(
    matchHistory: List<RpsMatch>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Match History", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        if (matchHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No matches played yet.")
            }
        } else {
            LazyColumn {
                items(matchHistory) { match ->
                    MatchRow(match)
                }
            }
        }
    }
}

@Composable
private fun MatchRow(match: RpsMatch) {
    val backgroundColor = when (match.result) {
        RpsResult.WIN -> MaterialTheme.colorScheme.secondaryContainer
        RpsResult.LOSE -> MaterialTheme.colorScheme.errorContainer
        RpsResult.DRAW -> MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Center lightning emoji
            Text(
                text = "⚡",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )

            // Player choice on the left
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("YOU", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = match.playerChoice.toEmoji(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // AI choice on the right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    text = match.aiChoice.toEmoji(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.width(4.dp))
                Text("AI", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/** Convert an RpsChoice to its emoji representation. */
private fun RpsChoice.toEmoji(): String = when (this) {
    RpsChoice.ROCK -> "🪨"
    RpsChoice.PAPER -> "📄"
    RpsChoice.SCISSORS -> "✂️"
}