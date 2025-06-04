package com.example.common.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RpsMatchEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MatchHistoryDatabase : RoomDatabase() {

    abstract fun matchHistoryDao(): MatchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MatchHistoryDatabase? = null

        fun getDatabase(context: Context): MatchHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MatchHistoryDatabase::class.java,
                    "match_history_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}