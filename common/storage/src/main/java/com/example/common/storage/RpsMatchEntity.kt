package com.example.common.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.common.game.RpsChoice
import com.example.common.game.RpsResult

@Entity(tableName = "match_history")
@TypeConverters(Converters::class)
data class RpsMatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerChoice: RpsChoice,
    val aiChoice: RpsChoice,
    val result: RpsResult,
    val timestamp: Long = System.currentTimeMillis() // Optional: to sort by time
)

// Helper function to map from domain model to entity
fun com.example.common.game.RpsMatch.toEntity(): RpsMatchEntity {
    return RpsMatchEntity(
        playerChoice = this.playerChoice,
        aiChoice = this.aiChoice,
        result = this.result
    )
}

// Helper function to map from entity to domain model
fun RpsMatchEntity.toDomain(): com.example.common.game.RpsMatch {
    return com.example.common.game.RpsMatch(
        playerChoice = this.playerChoice,
        aiChoice = this.aiChoice,
        result = this.result
    )
}