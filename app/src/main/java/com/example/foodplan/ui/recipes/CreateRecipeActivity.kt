package com.example.foodplan.ui.recipes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.data.RecipeRepository
import com.example.foodplan.model.Recipe
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CreateRecipeActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var cookingTimeEditText: TextInputEditText
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var instructionsRecyclerView: RecyclerView
    private lateinit var saveButton: MaterialButton

    private lateinit var ingredientsAdapter: EditableTextAdapter
    private lateinit var instructionsAdapter: EditableTextAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        // Инициализация views
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        cookingTimeEditText = findViewById(R.id.cookingTimeEditText)
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        instructionsRecyclerView = findViewById(R.id.instructionsRecyclerView)
        saveButton = findViewById(R.id.saveButton)

        // Настройка RecyclerViews
        ingredientsAdapter = EditableTextAdapter { position ->
            val currentList = ingredientsAdapter.getItems().toMutableList()
            currentList.removeAt(position)
            ingredientsAdapter.submitList(currentList)
        }
        ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = ingredientsAdapter
        }

        instructionsAdapter = EditableTextAdapter { position ->
            val currentList = instructionsAdapter.getItems().toMutableList()
            currentList.removeAt(position)
            instructionsAdapter.submitList(currentList)
        }
        instructionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = instructionsAdapter
        }

        // Настройка кнопок
        findViewById<MaterialButton>(R.id.addIngredientButton).setOnClickListener {
            val currentList = ingredientsAdapter.getItems().toMutableList()
            currentList.add("")
            ingredientsAdapter.submitList(currentList)
        }

        findViewById<MaterialButton>(R.id.addInstructionButton).setOnClickListener {
            val currentList = instructionsAdapter.getItems().toMutableList()
            currentList.add("")
            instructionsAdapter.submitList(currentList)
        }

        saveButton.setOnClickListener {
            if (validateInput()) {
                saveRecipe()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (nameEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите название рецепта", Toast.LENGTH_SHORT).show()
            return false
        }

        if (cookingTimeEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите время приготовления", Toast.LENGTH_SHORT).show()
            return false
        }

        val ingredients = ingredientsAdapter.getItems().filter { it.isNotBlank() }
        if (ingredients.isEmpty()) {
            Toast.makeText(this, "Добавьте хотя бы один ингредиент", Toast.LENGTH_SHORT).show()
            return false
        }

        val instructions = instructionsAdapter.getItems().filter { it.isNotBlank() }
        if (instructions.isEmpty()) {
            Toast.makeText(this, "Добавьте хотя бы один шаг приготовления", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveRecipe() {
        val recipe = Recipe(
            id = 0,
            name = nameEditText.text.toString(),
            description = descriptionEditText.text.toString(),
            cookingTime = cookingTimeEditText.text.toString().toInt(),
            calories = 0,
            servings = 0,
            imageUri = null,
            ingredients = ingredientsAdapter.getItems().filter { it.isNotBlank() },
            instructions = instructionsAdapter.getItems().filter { it.isNotBlank() }
        )

        RecipeRepository.addRecipe(recipe)
        Toast.makeText(this, "Рецепт сохранен", Toast.LENGTH_SHORT).show()
        finish()
    }
} 