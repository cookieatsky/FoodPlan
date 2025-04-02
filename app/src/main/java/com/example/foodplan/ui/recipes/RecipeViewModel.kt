package com.example.foodplan.ui.recipes

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.RecipeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository = RecipeRepository.getInstance(application)
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _recipeCreated = MutableLiveData<Boolean>()
    val recipeCreated: LiveData<Boolean> = _recipeCreated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            try {
                repository.getAllRecipes().collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при загрузке рецептов: ${e.message}"
            }
        }
    }

    suspend fun getRecipeById(id: Long): Recipe? {
        return try {
            Log.d("RecipeViewModel", "Getting recipe by ID: $id")
            val recipe = repository.getRecipeById(id)
            if (recipe != null) {
                Log.d("RecipeViewModel", "Found recipe: ${recipe.name}")
            } else {
                Log.d("RecipeViewModel", "Recipe not found for ID: $id")
            }
            recipe
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error getting recipe by ID: $id", e)
            _error.value = "Ошибка при загрузке рецепта: ${e.message}"
            null
        }
    }

    fun createRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.insertRecipe(recipe)
                _recipeCreated.value = true
                loadRecipes() // Перезагружаем список после создания
            } catch (e: Exception) {
                _error.value = "Ошибка при создании рецепта: ${e.message}"
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipe)
                _recipeCreated.value = true
                loadRecipes() // Перезагружаем список после обновления
            } catch (e: Exception) {
                _error.value = "Ошибка при обновлении рецепта: ${e.message}"
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)
                loadRecipes() // Перезагружаем список после удаления
            } catch (e: Exception) {
                _error.value = "Ошибка при удалении рецепта: ${e.message}"
            }
        }
    }

    fun importRecipes(recipes: List<Recipe>) {
        viewModelScope.launch {
            try {
                recipes.forEach { recipe ->
                    repository.insertRecipe(recipe)
                }
                loadRecipes() // Перезагружаем список после импорта
            } catch (e: Exception) {
                _error.value = "Ошибка при импорте рецептов: ${e.message}"
            }
        }
    }
} 