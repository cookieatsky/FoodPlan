package com.example.foodplan.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.model.Recipe
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class RecipeDetailsActivity : AppCompatActivity() {
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

    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Получаем рецепт из Intent
        recipe = intent.getParcelableExtra(EXTRA_RECIPE)

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
            recipe?.let { startEditRecipe(it) }
        }

        // Отображение данных рецепта
        displayRecipe()
    }

    private fun displayRecipe() {
        recipe?.let {
            recipeNameTextView.text = it.name
            recipeDescriptionTextView.text = it.description
            cookingTimeTextView.text = "Время приготовления: ${it.cookingTime} мин"
            caloriesTextView.text = "Калории: ${it.calories} ккал"
            servingsTextView.text = "Порций: ${it.servings}"
            
            // TODO: Загрузка изображения рецепта
            // recipeImageView.setImageURI(Uri.parse(it.imageUri))
            
            ingredientsAdapter.submitList(it.ingredients)
            instructionsAdapter.submitList(it.instructions)
        }
    }

    private fun startEditRecipe(recipe: Recipe) {
        val intent = Intent(this, CreateRecipeActivity::class.java).apply {
            putExtra(EXTRA_RECIPE, recipe)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_RECIPE = "extra_recipe"
    }
} 