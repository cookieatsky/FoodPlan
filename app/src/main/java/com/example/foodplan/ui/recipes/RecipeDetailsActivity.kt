package com.example.foodplan.ui.recipes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplan.R
import com.example.foodplan.model.Recipe
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class RecipeDetailsActivity : AppCompatActivity() {
    private val TAG = "RecipeDetailsActivity"
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeDescriptionTextView: TextView
    private lateinit var cookingTimeTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var servingsTextView: TextView
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var instructionsRecyclerView: RecyclerView
    private lateinit var editButton: ExtendedFloatingActionButton

    private lateinit var ingredientsAdapter: TextListAdapter
    private lateinit var instructionsAdapter: TextListAdapter

    private val viewModel: RecipeViewModel by viewModels()
    private var recipeId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Получаем ID рецепта из Intent
        recipeId = intent.getLongExtra("recipe_id", 0)
        Log.d(TAG, "Received recipe ID: $recipeId")

        // Инициализация views
        recipeImageView = findViewById(R.id.recipeImageView)
        recipeNameTextView = findViewById(R.id.recipeNameTextView)
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView)
        cookingTimeTextView = findViewById(R.id.cookingTimeTextView)
        caloriesTextView = findViewById(R.id.caloriesTextView)
        servingsTextView = findViewById(R.id.servingsTextView)
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        instructionsRecyclerView = findViewById(R.id.instructionsRecyclerView)
        editButton = findViewById(R.id.editButton)

        // Настройка RecyclerViews
        ingredientsAdapter = TextListAdapter()
        ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipeDetailsActivity)
            adapter = ingredientsAdapter
        }

        instructionsAdapter = TextListAdapter()
        instructionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipeDetailsActivity)
            adapter = instructionsAdapter
        }

        // Настройка кнопки редактирования
        editButton.setOnClickListener {
            Log.d(TAG, "Edit button clicked for recipe ID: $recipeId")
            startEditRecipe()
        }

        // Загрузка данных рецепта
        loadRecipe()
    }

    private fun loadRecipe() {
        lifecycleScope.launch {
            Log.d(TAG, "Loading recipe with ID: $recipeId")
            val recipe = viewModel.getRecipeById(recipeId)
            if (recipe != null) {
                Log.d(TAG, "Recipe loaded successfully: ${recipe.name}")
                displayRecipe(recipe)
            } else {
                Log.e(TAG, "Failed to load recipe with ID: $recipeId")
            }
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        Log.d(TAG, "Displaying recipe: ${recipe.name}")
        recipeNameTextView.text = recipe.name
        recipeDescriptionTextView.text = recipe.description
        cookingTimeTextView.text = "Время приготовления: ${recipe.cookingTime} мин"
        caloriesTextView.text = "Калории: ${recipe.calories} ккал"
        servingsTextView.text = "Порций: ${recipe.servings}"
        
        recipe.imageUri?.let { uriString ->
            try {
                Log.d(TAG, "Setting recipe image from URI: $uriString")
                val uri = Uri.parse(uriString)
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_recipe_placeholder)
                    .error(R.drawable.ic_recipe_placeholder)
                    .centerCrop()
                    .into(recipeImageView)
                Log.d(TAG, "Recipe image set successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting recipe image", e)
                recipeImageView.setImageResource(R.drawable.ic_recipe_placeholder)
            }
        } ?: run {
            Log.d(TAG, "No image URI for recipe")
            recipeImageView.setImageResource(R.drawable.ic_recipe_placeholder)
        }
        
        ingredientsAdapter.submitList(recipe.ingredients)
        instructionsAdapter.submitList(recipe.instructions)
    }

    private fun startEditRecipe() {
        Log.d(TAG, "Starting edit activity for recipe ID: $recipeId")
        val intent = Intent(this, CreateRecipeActivity::class.java).apply {
            putExtra("recipe_id", recipeId)
        }
        startActivity(intent)
    }
} 