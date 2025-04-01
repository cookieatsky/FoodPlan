package com.example.foodplan.data.converter

import androidx.room.TypeConverter
import com.example.foodplan.model.Gender

class GenderConverter {
    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(value: String): Gender {
        return Gender.valueOf(value)
    }
} 