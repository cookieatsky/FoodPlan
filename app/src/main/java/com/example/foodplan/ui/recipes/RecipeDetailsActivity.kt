package com.example.foodplan.ui.recipes

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.data.RecipeRepository
import com.example.foodplan.model.Recipe
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeDescriptionTextView: TextView
    private lateinit var cookingTimeTextView: TextView
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
        recipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RECIPE, Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RECIPE)
        }

        // Инициализация views
        recipeNameTextView = findViewById(R.id.recipeNameTextView)
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView)
        cookingTimeTextView = findViewById(R.id.cookingTimeTextView)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Удаление рецепта")
            .setMessage("Вы уверены, что хотите удалить этот рецепт?")
            .setPositiveButton("Удалить") { _, _ ->
                recipe?.let {
                    RecipeRepository.deleteRecipe(it)
                    finish()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    companion object {
        const val EXTRA_RECIPE = "extra_recipe"
    }
} 