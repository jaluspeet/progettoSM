package com.example.common.storage

import androidx.room.TypeConverter
import com.example.common.game.RpsChoice
import com.example.common.game.RpsResult

class Converters {
    @TypeConverter
    fun fromRpsChoice(value: RpsChoice?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRpsChoice(value: String?): RpsChoice? {
        return value?.let { RpsChoice.valueOf(it) }
    }

    @TypeConverter
    fun fromRpsResult(value: RpsResult?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRpsResult(value: String?): RpsResult? {
        return value?.let { RpsResult.valueOf(it) }
    }
}