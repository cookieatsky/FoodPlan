package com.example.foodplan.ui.recipes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.databinding.ActivityCreateRecipeBinding
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.RecipeRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.example.foodplan.utils.PermissionUtils
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import android.util.Log
import com.bumptech.glide.Glide

class CreateRecipeActivity : AppCompatActivity() {
    private val TAG = "CreateRecipeActivity"
    private lateinit var binding: ActivityCreateRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var recipeId: Long = 0

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

    private lateinit var breakfastCheckBox: MaterialCheckBox
    private lateinit var lunchCheckBox: MaterialCheckBox
    private lateinit var dinnerCheckBox: MaterialCheckBox
    private lateinit var snackCheckBox: MaterialCheckBox

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Для добавления изображения необходимо разрешение", Toast.LENGTH_LONG).show()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.recipeImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeRepository = RecipeRepository.getInstance(this)

        // Получаем ID рецепта из Intent
        recipeId = intent.getLongExtra("recipe_id", 0)
        Log.d(TAG, "Received recipe ID in CreateRecipeActivity: $recipeId")

        // Инициализация views
        nameEditText = binding.nameEditText
        descriptionEditText = binding.descriptionEditText
        cookingTimeEditText = binding.cookingTimeEditText
        caloriesEditText = binding.caloriesEditText
        servingsEditText = binding.servingsEditText
        ingredientsRecyclerView = binding.ingredientsRecyclerView
        instructionsRecyclerView = binding.instructionsRecyclerView
        saveButton = binding.saveButton

        breakfastCheckBox = binding.breakfastCheckBox
        lunchCheckBox = binding.lunchCheckBox
        dinnerCheckBox = binding.dinnerCheckBox
        snackCheckBox = binding.snackCheckBox

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

        setupClickListeners()
        setupObservers()

        // Если это редактирование, загружаем данные рецепта
        if (recipeId != 0L) {
            Log.d(TAG, "Loading recipe for editing with ID: $recipeId")
            loadRecipe()
        }
    }

    private fun setupClickListeners() {
        binding.addImageButton.setOnClickListener {
            checkAndRequestPermission()
        }

        binding.addIngredientButton.setOnClickListener {
            val adapter = binding.ingredientsRecyclerView.adapter as EditableTextAdapter
            val currentList = adapter.getItems().toMutableList()
            currentList.add("")
            adapter.submitList(currentList)
        }

        binding.addInstructionButton.setOnClickListener {
            val adapter = binding.instructionsRecyclerView.adapter as EditableTextAdapter
            val currentList = adapter.getItems().toMutableList()
            currentList.add("")
            adapter.submitList(currentList)
        }

        binding.saveButton.setOnClickListener {
            saveRecipe()
        }
    }

    private fun setupObservers() {
        viewModel.recipeCreated.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Рецепт успешно создан", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun loadRecipe() {
        lifecycleScope.launch {
            Log.d(TAG, "Starting to load recipe with ID: $recipeId")
            val recipe = viewModel.getRecipeById(recipeId)
            if (recipe != null) {
                Log.d(TAG, "Successfully loaded recipe: ${recipe.name}")
                displayRecipe(recipe)
            } else {
                Log.e(TAG, "Failed to load recipe with ID: $recipeId")
                Toast.makeText(this@CreateRecipeActivity, 
                    "Не удалось загрузить рецепт", 
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        Log.d(TAG, "Displaying recipe: ${recipe.name}")
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

        recipe.imageUri?.let { uriString ->
            try {
                Log.d(TAG, "Loading recipe image from URI: $uriString")
                selectedImageUri = Uri.parse(uriString)
                Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_recipe_placeholder)
                    .error(R.drawable.ic_recipe_placeholder)
                    .centerCrop()
                    .into(binding.recipeImageView)
                Log.d(TAG, "Recipe image loaded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recipe image", e)
                binding.recipeImageView.setImageResource(R.drawable.ic_recipe_placeholder)
            }
        } ?: run {
            Log.d(TAG, "No image URI for recipe")
            binding.recipeImageView.setImageResource(R.drawable.ic_recipe_placeholder)
        }
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

    private fun checkAndRequestPermission() {
        if (PermissionUtils.hasStoragePermission(this)) {
            openImagePicker()
        } else {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun saveRecipe() {
        if (validateInputs()) {
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val cookingTime = cookingTimeEditText.text.toString().toIntOrNull() ?: 0
            val calories = caloriesEditText.text.toString().toIntOrNull() ?: 0
            val servings = servingsEditText.text.toString().toIntOrNull() ?: 0
            val ingredients = ingredientsAdapter.getItems().filter { it.isNotBlank() }
            val instructions = instructionsAdapter.getItems().filter { it.isNotBlank() }

            val imageUriString = selectedImageUri?.toString()
            Log.d(TAG, "Saving recipe with image URI: $imageUriString")

            val recipe = Recipe(
                id = recipeId,
                name = name,
                description = description,
                cookingTime = cookingTime,
                calories = calories,
                servings = servings,
                imageUri = imageUriString,
                ingredients = ingredients,
                instructions = instructions,
                isBreakfast = breakfastCheckBox.isChecked,
                isLunch = lunchCheckBox.isChecked,
                isDinner = dinnerCheckBox.isChecked,
                isSnack = snackCheckBox.isChecked
            )

            lifecycleScope.launch {
                if (recipeId == 0L) {
                    viewModel.createRecipe(recipe)
                } else {
                    viewModel.updateRecipe(recipe)
                }
                finish()
            }
        }
    }
} 