package com.example.common.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: RpsMatchEntity)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC")
    fun getAllMatches(): Flow<List<RpsMatchEntity>>

    @Query("DELETE FROM match_history")
    suspend fun clearAllMatches()
}