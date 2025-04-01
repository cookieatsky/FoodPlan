package com.example.foodplan.data

import com.example.foodplan.model.Recipe

object RecipeRepository {
    private val recipes = mutableListOf<Recipe>()

    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
    }

    fun getRecipes(): List<Recipe> = recipes.toList()

    fun deleteRecipe(recipe: Recipe) {
        recipes.remove(recipe)
    }
} 