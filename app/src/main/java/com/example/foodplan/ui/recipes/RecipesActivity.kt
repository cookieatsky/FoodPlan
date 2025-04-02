package com.example.foodplan.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodplan.databinding.ActivityRecipesBinding
import com.example.foodplan.model.Recipe
import kotlinx.coroutines.launch

class RecipesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipesBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadRecipes()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipe ->
                val intent = Intent(this, RecipeDetailsActivity::class.java)
                intent.putExtra("recipe_id", recipe.id)
                startActivity(intent)
            },
            onRecipeDelete = { recipe ->
                viewModel.deleteRecipe(recipe)
            }
        )
        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipesActivity)
            adapter = recipeAdapter
        }
    }

    private fun setupObservers() {
        viewModel.recipes.observe(this) { recipes ->
            recipeAdapter.submitList(recipes)
        }

        viewModel.error.observe(this) { error ->
            // Показать ошибку пользователю
        }
    }

    private fun setupClickListeners() {
        binding.addRecipeFab.setOnClickListener {
            startActivity(Intent(this, CreateRecipeActivity::class.java))
        }
    }

    private fun loadRecipes() {
        lifecycleScope.launch {
            viewModel.loadRecipes()
        }
    }
} 