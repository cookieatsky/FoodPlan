package com.example.foodplan.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.foodplan.model.Recipe

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val cookingTime: Int,
    val calories: Int,
    val servings: Int,
    val ingredients: List<String>,
    val instructions: List<String>,
    val imageUri: String?,
    val isBreakfast: Boolean = false,
    val isLunch: Boolean = false,
    val isDinner: Boolean = false,
    val isSnack: Boolean = false
) {
    fun toRecipe(): Recipe {
        return Recipe(
            id = id,
            name = name,
            description = description,
            cookingTime = cookingTime,
            calories = calories,
            servings = servings,
            ingredients = ingredients,
            instructions = instructions,
            imageUri = imageUri,
            isBreakfast = isBreakfast,
            isLunch = isLunch,
            isDinner = isDinner,
            isSnack = isSnack
        )
    }

    companion object {
        fun fromRecipe(recipe: Recipe): RecipeEntity {
            return RecipeEntity(
                id = recipe.id,
                name = recipe.name,
                description = recipe.description,
                cookingTime = recipe.cookingTime,
                calories = recipe.calories,
                servings = recipe.servings,
                ingredients = recipe.ingredients,
                instructions = recipe.instructions,
                imageUri = recipe.imageUri,
                isBreakfast = recipe.isBreakfast,
                isLunch = recipe.isLunch,
                isDinner = recipe.isDinner,
                isSnack = recipe.isSnack
            )
        }
    }
} 