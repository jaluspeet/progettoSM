package com.example.common.storage

import android.content.Context

/**
 * Initializes and provides the MatchHistoryRepository without forcing features to depend on the App module.
 */
object ServiceLocator {
    private var repository: MatchHistoryRepository? = null

    /** Call this once from your Application.onCreate() */
    fun init(context: Context) {
        if (repository == null) {
            val db = MatchHistoryDatabase.getDatabase(context)
            repository = MatchHistoryRepository(db.matchHistoryDao())
        }
    }

    /** Features call this to get the singleton repository instance */
    fun provideMatchHistoryRepository(): MatchHistoryRepository {
        return repository
            ?: throw IllegalStateException("ServiceLocator not initialized. Call ServiceLocator.init(context) in Application.onCreate().")
    }
}