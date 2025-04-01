package com.example.foodplan.ui.recipes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.RecipeRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.launch

class CreateRecipeActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var cookingTimeEditText: TextInputEditText
    private lateinit var caloriesEditText: TextInputEditText
    private lateinit var servingsEditText: TextInputEditText
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var instructionsRecyclerView: RecyclerView
    private lateinit var saveButton: MaterialButton
    private lateinit var recipeRepository: RecipeRepository

    private lateinit var ingredientsAdapter: EditableTextAdapter
    private lateinit var instructionsAdapter: EditableTextAdapter

    private var editingRecipe: Recipe? = null

    private lateinit var breakfastCheckBox: MaterialCheckBox
    private lateinit var lunchCheckBox: MaterialCheckBox
    private lateinit var dinnerCheckBox: MaterialCheckBox
    private lateinit var snackCheckBox: MaterialCheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        recipeRepository = RecipeRepository.getInstance(this)

        // Инициализация views
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        cookingTimeEditText = findViewById(R.id.cookingTimeEditText)
        caloriesEditText = findViewById(R.id.caloriesEditText)
        servingsEditText = findViewById(R.id.servingsEditText)
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        instructionsRecyclerView = findViewById(R.id.instructionsRecyclerView)
        saveButton = findViewById(R.id.saveButton)

        breakfastCheckBox = findViewById(R.id.breakfastCheckBox)
        lunchCheckBox = findViewById(R.id.lunchCheckBox)
        dinnerCheckBox = findViewById(R.id.dinnerCheckBox)
        snackCheckBox = findViewById(R.id.snackCheckBox)

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

        // Получаем рецепт для редактирования, если он есть
        editingRecipe = intent.getParcelableExtra(RecipeDetailsActivity.EXTRA_RECIPE)
        if (editingRecipe != null) {
            fillRecipeData(editingRecipe!!)
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
            if (validateInputs()) {
                saveRecipe()
            }
        }
    }

    private fun fillRecipeData(recipe: Recipe) {
        nameEditText.setText(recipe.name)
        descriptionEditText.setText(recipe.description)
        cookingTimeEditText.setText(recipe.cookingTime.toString())
        caloriesEditText.setText(recipe.calories.toString())
        servingsEditText.setText(recipe.servings.toString())
        ingredientsAdapter.submitList(recipe.ingredients.toMutableList())
        instructionsAdapter.submitList(recipe.instructions.toMutableList())

        breakfastCheckBox.isChecked = recipe.isBreakfast
        lunchCheckBox.isChecked = recipe.isLunch
        dinnerCheckBox.isChecked = recipe.isDinner
        snackCheckBox.isChecked = recipe.isSnack
    }

    private fun validateInputs(): Boolean {
        if (nameEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите название рецепта", Toast.LENGTH_SHORT).show()
            return false
        }

        if (cookingTimeEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите время приготовления", Toast.LENGTH_SHORT).show()
            return false
        }

        if (caloriesEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите количество калорий", Toast.LENGTH_SHORT).show()
            return false
        }

        if (servingsEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите количество порций", Toast.LENGTH_SHORT).show()
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

        if (!breakfastCheckBox.isChecked && !lunchCheckBox.isChecked && 
            !dinnerCheckBox.isChecked && !snackCheckBox.isChecked) {
            Toast.makeText(this, "Выберите хотя бы один тип блюда", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveRecipe() {
        val recipe = Recipe(
            id = editingRecipe?.id ?: 0,
            name = nameEditText.text.toString(),
            description = descriptionEditText.text.toString(),
            cookingTime = cookingTimeEditText.text.toString().toInt(),
            calories = caloriesEditText.text.toString().toInt(),
            servings = servingsEditText.text.toString().toInt(),
            imageUri = null,
            ingredients = ingredientsAdapter.getItems().filter { it.isNotBlank() },
            instructions = instructionsAdapter.getItems().filter { it.isNotBlank() },
            isBreakfast = breakfastCheckBox.isChecked,
            isLunch = lunchCheckBox.isChecked,
            isDinner = dinnerCheckBox.isChecked,
            isSnack = snackCheckBox.isChecked
        )

        lifecycleScope.launch {
            if (editingRecipe != null) {
                recipeRepository.updateRecipe(recipe)
                Toast.makeText(this@CreateRecipeActivity, "Рецепт обновлен", Toast.LENGTH_SHORT).show()
            } else {
                recipeRepository.insertRecipe(recipe)
                Toast.makeText(this@CreateRecipeActivity, "Рецепт сохранен", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
} 