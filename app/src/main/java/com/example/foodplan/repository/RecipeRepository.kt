package com.example.foodplan.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.foodplan.database.AppDatabase
import com.example.foodplan.database.RecipeDao
import com.example.foodplan.database.RecipeEntity
import com.example.foodplan.model.Recipe
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository private constructor(context: Context) {
    private val TAG = "RecipeRepository"
    private val recipeDao = AppDatabase.getInstance(context).recipeDao()
    private val context = context.applicationContext

    fun getAllRecipes(): Flow<List<Recipe>> {
        Log.d(TAG, "Getting all recipes")
        return recipeDao.getAllRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    suspend fun getRecipeById(id: Long): Recipe? {
        Log.d(TAG, "Getting recipe by ID: $id")
        val entity = recipeDao.getRecipeById(id)
        if (entity != null) {
            Log.d(TAG, "Found recipe: ${entity.name}")
            return entity.toRecipe()
        } else {
            Log.e(TAG, "Recipe not found for ID: $id")
            return null
        }
    }

    suspend fun insertRecipe(recipe: Recipe) {
        Log.d(TAG, "Inserting recipe: ${recipe.name}")
        try {
            recipeDao.insertRecipe(RecipeEntity.fromRecipe(recipe))
            Log.d(TAG, "Recipe inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting recipe", e)
            throw e
        }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        Log.d(TAG, "Updating recipe: ${recipe.name} (ID: ${recipe.id})")
        try {
            recipeDao.updateRecipe(RecipeEntity.fromRecipe(recipe))
            Log.d(TAG, "Recipe updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating recipe", e)
            throw e
        }
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        Log.d(TAG, "Deleting recipe: ${recipe.name} (ID: ${recipe.id})")
        try {
            recipeDao.deleteRecipe(RecipeEntity.fromRecipe(recipe))
            Log.d(TAG, "Recipe deleted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe", e)
            throw e
        }
    }

    private fun saveImage(uriString: String): String {
        val uri = Uri.parse(uriString)
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "${UUID.randomUUID()}.jpg"
        val outputFile = File(context.filesDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }

        return outputFile.absolutePath
    }

    private fun deleteImage(uriString: String) {
        try {
            File(uriString).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RecipeRepository? = null

        fun getInstance(context: Context): RecipeRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = RecipeRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
} 