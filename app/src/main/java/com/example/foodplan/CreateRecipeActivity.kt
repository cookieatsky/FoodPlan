package com.example.foodplan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Для добавления изображения необходим доступ к галерее", Toast.LENGTH_LONG).show()
        }
    }

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
        binding.addImageButton.setOnClickListener {
            checkPermissionAndPickImage()
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

    private fun checkPermissionAndPickImage() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun saveRecipe() {
        val name = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val cookingTime = binding.cookingTimeEditText.text.toString().toIntOrNull() ?: 0
        val calories = binding.caloriesEditText.text.toString().toIntOrNull() ?: 0
        val servings = binding.servingsEditText.text.toString().toIntOrNull() ?: 1
        val imageUri = selectedImageUri?.toString()
        val isBreakfast = binding.breakfastCheckBox.isChecked
        val isLunch = binding.lunchCheckBox.isChecked
        val isDinner = binding.dinnerCheckBox.isChecked
        val isSnack = binding.snackCheckBox.isChecked

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

        if (!isBreakfast && !isLunch && !isDinner && !isSnack) {
            Toast.makeText(this, "Выберите хотя бы один тип блюда", Toast.LENGTH_SHORT).show()
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
            instructions = instructionsAdapter.currentList.filter { it.isNotBlank() },
            isBreakfast = isBreakfast,
            isLunch = isLunch,
            isDinner = isDinner,
            isSnack = isSnack
        )

        lifecycleScope.launch {
            recipeRepository.insertRecipe(recipe)
            Toast.makeText(this@CreateRecipeActivity, "Рецепт сохранен", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 