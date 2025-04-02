package com.example.foodplan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodplan.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val cookingTime: Int = 0,
    val calories: Int = 0,
    val servings: Int = 1,
    val imageUri: String? = null,
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
            ingredients = ingredients,
            instructions = instructions,
            cookingTime = cookingTime,
            calories = calories,
            servings = servings,
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
                ingredients = recipe.ingredients,
                instructions = recipe.instructions,
                cookingTime = recipe.cookingTime,
                calories = recipe.calories,
                servings = recipe.servings,
                imageUri = recipe.imageUri,
                isBreakfast = recipe.isBreakfast,
                isLunch = recipe.isLunch,
                isDinner = recipe.isDinner,
                isSnack = recipe.isSnack
            )
        }
    }
} 