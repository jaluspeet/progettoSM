package com.example.common.game

enum class RpsChoice {
    ROCK, PAPER, SCISSORS
}

enum class RpsResult {
    WIN, LOSE, DRAW
}

data class RpsMatch(
    val playerChoice: RpsChoice,
    val aiChoice: RpsChoice,
    val result: RpsResult
)

object RpsGame {
    fun play(playerChoice: RpsChoice): RpsMatch {
        val aiChoice = randomChoice()
        val result = determineResult(playerChoice, aiChoice)
        return RpsMatch(playerChoice, aiChoice, result)
    }

    private fun randomChoice(): RpsChoice {
        return RpsChoice.entries.toTypedArray().random()
    }

    private fun determineResult(player: RpsChoice, ai: RpsChoice): RpsResult {
        return when {
            player == ai -> RpsResult.DRAW
            (player == RpsChoice.ROCK && ai == RpsChoice.SCISSORS) ||
                    (player == RpsChoice.PAPER && ai == RpsChoice.ROCK) ||
                    (player == RpsChoice.SCISSORS && ai == RpsChoice.PAPER) -> RpsResult.WIN
            else -> RpsResult.LOSE
        }
    }
}
