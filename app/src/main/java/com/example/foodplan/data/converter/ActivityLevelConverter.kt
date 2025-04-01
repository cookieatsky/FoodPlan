package com.example.foodplan.data.converter

import androidx.room.TypeConverter
import com.example.foodplan.model.ActivityLevel

class ActivityLevelConverter {
    @TypeConverter
    fun fromActivityLevel(activityLevel: ActivityLevel): String {
        return activityLevel.name
    }

    @TypeConverter
    fun toActivityLevel(value: String): ActivityLevel {
        return ActivityLevel.valueOf(value)
    }
} 