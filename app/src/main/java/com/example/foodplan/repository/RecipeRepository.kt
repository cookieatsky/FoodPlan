package com.example.foodplan.repository

import android.content.Context
import com.example.foodplan.data.AppDatabase
import com.example.foodplan.data.RecipeEntity
import com.example.foodplan.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository private constructor(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val recipeDao = database.recipeDao()

    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    fun getRecipeById(id: Long): Flow<Recipe?> {
        return recipeDao.getRecipeById(id).map { entity ->
            entity?.toRecipe()
        }
    }

    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(RecipeEntity.fromRecipe(recipe))
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(RecipeEntity.fromRecipe(recipe))
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(RecipeEntity.fromRecipe(recipe))
    }

    companion object {
        @Volatile
        private var INSTANCE: RecipeRepository? = null

        fun getInstance(context: Context): RecipeRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = RecipeRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
} 