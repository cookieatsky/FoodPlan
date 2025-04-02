package com.example.foodplan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [RecipeEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Проверяем существование колонок перед добавлением
                val cursor = database.query("PRAGMA table_info(recipes)")
                val columns = mutableSetOf<String>()
                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(1))
                }
                cursor.close()

                if (!columns.contains("isBreakfast")) {
                    database.execSQL("""
                        ALTER TABLE recipes 
                        ADD COLUMN isBreakfast INTEGER NOT NULL DEFAULT 0
                    """)
                }
                if (!columns.contains("isLunch")) {
                    database.execSQL("""
                        ALTER TABLE recipes 
                        ADD COLUMN isLunch INTEGER NOT NULL DEFAULT 0
                    """)
                }
                if (!columns.contains("isDinner")) {
                    database.execSQL("""
                        ALTER TABLE recipes 
                        ADD COLUMN isDinner INTEGER NOT NULL DEFAULT 0
                    """)
                }
                if (!columns.contains("isSnack")) {
                    database.execSQL("""
                        ALTER TABLE recipes 
                        ADD COLUMN isSnack INTEGER NOT NULL DEFAULT 0
                    """)
                }
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_plan_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
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
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
} 