package com.example.foodplan.ui.recipes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.RecipeRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class RecipesActivity : AppCompatActivity() {
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var addRecipeFab: FloatingActionButton
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeRepository: RecipeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        recipeRepository = RecipeRepository.getInstance(this)

        // Инициализация views
        recipesRecyclerView = findViewById(R.id.recipesRecyclerView)
        addRecipeFab = findViewById(R.id.addRecipeFab)

        // Настройка RecyclerView
        recipeAdapter = RecipeAdapter { recipe ->
            val intent = Intent(this, RecipeDetailsActivity::class.java).apply {
                putExtra(RecipeDetailsActivity.EXTRA_RECIPE, recipe)
            }
            startActivity(intent)
        }
        recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipesActivity)
            adapter = recipeAdapter
        }

        // Настройка FAB
        addRecipeFab.setOnClickListener {
            startActivity(Intent(this, CreateRecipeActivity::class.java))
        }

        // Загрузка рецептов
        loadRecipes()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем список при возвращении на экран
        loadRecipes()
    }

    private fun loadRecipes() {
        lifecycleScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                recipeAdapter.submitList(recipes)
            }
        }
    }
} 