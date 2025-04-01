package com.example.foodplan.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonParseException

class StringListConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromString(value: String?): List<String> {
        return try {
            if (value.isNullOrEmpty()) {
                emptyList()
            } else {
                gson.fromJson(value, type)
            }
        } catch (e: JsonParseException) {
            emptyList()
        }
    }

    @TypeConverter
    fun toString(list: List<String>?): String {
        return try {
            if (list.isNullOrEmpty()) {
                "[]"
            } else {
                gson.toJson(list)
            }
        } catch (e: Exception) {
            "[]"
        }
    }
} 