package com.example.feature.scoreboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common.game.RpsMatch

// Helper to convert choice to emoji
fun RpsMatch.choiceToEmoji(choice: com.example.common.game.RpsChoice): String = when (choice) {
    com.example.common.game.RpsChoice.ROCK -> "🪨"
    com.example.common.game.RpsChoice.PAPER -> "📄"
    com.example.common.game.RpsChoice.SCISSORS -> "✂️"
}

@Composable
fun ScoreboardScreen(
    modifier: Modifier = Modifier,
    matchHistory: List<RpsMatch> = listOf()
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Match History", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        if (matchHistory.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
fun MatchRow(match: RpsMatch) {
    val backgroundColor = when (match.result) {
        com.example.common.game.RpsResult.WIN -> MaterialTheme.colorScheme.secondaryContainer // greenish
        com.example.common.game.RpsResult.LOSE -> MaterialTheme.colorScheme.errorContainer // red
        com.example.common.game.RpsResult.DRAW -> MaterialTheme.colorScheme.primaryContainer // blueish
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Lightning in exact center
            Text(
                "⚡",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )

            // YOU on the left
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("YOU", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(4.dp))
                Text(match.choiceToEmoji(match.playerChoice), style = MaterialTheme.typography.headlineMedium)
            }

            // AI on the right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(match.choiceToEmoji(match.aiChoice), style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(4.dp))
                Text("AI", style = MaterialTheme.typography.labelMedium)
            }
        }
    }

}

