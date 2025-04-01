package com.example.foodplan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.databinding.ActivityCreateRecipeBinding
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.RecipeRepository
import com.example.foodplan.ui.recipes.EditableTextAdapter
import kotlinx.coroutines.launch

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRecipeBinding
    private lateinit var recipeRepository: RecipeRepository
    private var selectedImageUri: Uri? = null

    private lateinit var ingredientsAdapter: EditableTextAdapter
    private lateinit var instructionsAdapter: EditableTextAdapter

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.recipeImageView.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeRepository = RecipeRepository.getInstance(this)

        setupRecyclerViews()
        setupClickListeners()
    }

    private fun setupRecyclerViews() {
        ingredientsAdapter = EditableTextAdapter { position ->
            val currentList = ingredientsAdapter.currentList.toMutableList()
            currentList.removeAt(position)
            ingredientsAdapter.submitList(currentList)
        }
        binding.ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = ingredientsAdapter
        }

        instructionsAdapter = EditableTextAdapter { position ->
            val currentList = instructionsAdapter.currentList.toMutableList()
            currentList.removeAt(position)
            instructionsAdapter.submitList(currentList)
        }
        binding.instructionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = instructionsAdapter
        }

        // Добавляем пустые строки для начала
        ingredientsAdapter.submitList(listOf(""))
        instructionsAdapter.submitList(listOf(""))
    }

    private fun setupClickListeners() {
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        binding.addIngredientButton.setOnClickListener {
            val currentList = ingredientsAdapter.currentList.toMutableList()
            currentList.add("")
            ingredientsAdapter.submitList(currentList)
        }

        binding.addInstructionButton.setOnClickListener {
            val currentList = instructionsAdapter.currentList.toMutableList()
            currentList.add("")
            instructionsAdapter.submitList(currentList)
        }

        binding.saveButton.setOnClickListener {
            saveRecipe()
        }
    }

    private fun saveRecipe() {
        val name = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val cookingTime = binding.cookingTimeEditText.text.toString().toIntOrNull() ?: 0
        val calories = binding.caloriesEditText.text.toString().toIntOrNull() ?: 0
        val servings = binding.servingsEditText.text.toString().toIntOrNull() ?: 1
        val imageUri = selectedImageUri?.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Введите название рецепта", Toast.LENGTH_SHORT).show()
            return
        }

        if (cookingTime <= 0) {
            Toast.makeText(this, "Введите время приготовления", Toast.LENGTH_SHORT).show()
            return
        }

        if (calories <= 0) {
            Toast.makeText(this, "Введите количество калорий", Toast.LENGTH_SHORT).show()
            return
        }

        if (servings <= 0) {
            Toast.makeText(this, "Введите количество порций", Toast.LENGTH_SHORT).show()
            return
        }

        val recipe = Recipe(
            id = 0,
            name = name,
            description = description,
            cookingTime = cookingTime,
            calories = calories,
            servings = servings,
            imageUri = imageUri,
            ingredients = ingredientsAdapter.currentList.filter { it.isNotBlank() },
            instructions = instructionsAdapter.currentList.filter { it.isNotBlank() }
        )

        lifecycleScope.launch {
            recipeRepository.insertRecipe(recipe)
            Toast.makeText(this@CreateRecipeActivity, "Рецепт сохранен", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 