package com.example.common.storage

import com.example.common.game.RpsMatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchHistoryRepository(private val matchHistoryDao: MatchHistoryDao) {

    val allMatches: Flow<List<RpsMatch>> =
        matchHistoryDao.getAllMatches().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun insertMatch(match: RpsMatch) {
        matchHistoryDao.insertMatch(match.toEntity())
    }

    suspend fun clearAllMatches() {
        matchHistoryDao.clearAllMatches()
    }
}