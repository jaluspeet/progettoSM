package com.example.feature.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common.game.RpsChoice
import com.example.common.game.RpsMatch
import com.example.common.game.RpsResult

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
            Text(
                text = "⚡",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )

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

private fun RpsChoice.toEmoji(): String = when (this) {
    RpsChoice.ROCK -> "🪨"
    RpsChoice.PAPER -> "📄"
    RpsChoice.SCISSORS -> "✂️"
}