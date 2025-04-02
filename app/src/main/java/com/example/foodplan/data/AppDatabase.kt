package com.example.foodplan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodplan.data.converter.ActivityLevelConverter
import com.example.foodplan.data.converter.GenderConverter
import com.example.foodplan.data.converter.StringListConverter

@Database(
    entities = [
        RecipeEntity::class,
        UserProfileEntity::class,
        ShoppingListItemEntity::class
    ],
    version = 2
)
@TypeConverters(
    GenderConverter::class,
    ActivityLevelConverter::class,
    StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun shoppingListDao(): ShoppingListItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_plan_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 