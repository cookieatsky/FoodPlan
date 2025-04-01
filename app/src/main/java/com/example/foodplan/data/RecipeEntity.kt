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
    val imageUri: String?,
    val cookingTime: Int,
    val calories: Int,
    val servings: Int,
    val ingredients: List<String>,
    val instructions: List<String>
) {
    fun toRecipe(): Recipe {
        return Recipe(
            id = id,
            name = name,
            description = description,
            imageUri = imageUri,
            cookingTime = cookingTime,
            calories = calories,
            servings = servings,
            ingredients = ingredients,
            instructions = instructions
        )
    }

    companion object {
        fun fromRecipe(recipe: Recipe): RecipeEntity {
            return RecipeEntity(
                id = recipe.id,
                name = recipe.name,
                description = recipe.description,
                imageUri = recipe.imageUri,
                cookingTime = recipe.cookingTime,
                calories = recipe.calories,
                servings = recipe.servings,
                ingredients = recipe.ingredients,
                instructions = recipe.instructions
            )
        }
    }
} 