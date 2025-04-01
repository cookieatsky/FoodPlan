package com.example.foodplan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecipeEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_plan_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@androidx.room.TypeConverters
class Converters {
    @androidx.room.TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",")
    }

    @androidx.room.TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
} 