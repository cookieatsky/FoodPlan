package com.example.foodplan.ui.recipes

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodplan.databinding.ActivityImportRecipesBinding
import com.example.foodplan.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ImportRecipesActivity : AppCompatActivity() {
    private val TAG = "ImportRecipesActivity"
    private lateinit var binding: ActivityImportRecipesBinding
    private val viewModel: RecipeViewModel by viewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { 
            Log.d(TAG, "Selected file URI: $uri")
            importRecipesFromExcel(it) 
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectFileButton.setOnClickListener {
            getContent.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun importRecipesFromExcel(uri: android.net.Uri) {
        lifecycleScope.launch {
            try {
                val recipes = withContext(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(uri)
                    Log.d(TAG, "Starting to parse Excel file")
                    val parsedRecipes = parseExcelFile(inputStream)
                    Log.d(TAG, "Parsed ${parsedRecipes.size} recipes")
                    parsedRecipes
                }

                if (recipes.isEmpty()) {
                    Toast.makeText(this@ImportRecipesActivity, 
                        "Не удалось найти рецепты в файле", 
                        Toast.LENGTH_LONG).show()
                    return@launch
                }

                viewModel.importRecipes(recipes)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ImportRecipesActivity, 
                        "Успешно импортировано ${recipes.size} рецептов", 
                        Toast.LENGTH_LONG).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error importing recipes", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ImportRecipesActivity, 
                        "Ошибка при импорте рецептов: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseExcelFile(inputStream: InputStream?): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        
        Log.d(TAG, "Excel file has ${sheet.lastRowNum + 1} rows")

        // Пропускаем заголовок
        for (rowIndex in 1..sheet.lastRowNum) {
            try {
                val row = sheet.getRow(rowIndex)
                if (row == null) {
                    Log.d(TAG, "Row $rowIndex is null")
                    continue
                }

                // Получаем значения ячеек, преобразуя их в нужный формат
                val nameCell = row.getCell(0)
                if (nameCell == null || nameCell.stringCellValue.isBlank()) {
                    Log.d(TAG, "Name cell is null or empty at row $rowIndex")
                    continue
                }
                val name = nameCell.stringCellValue.trim()

                val descriptionCell = row.getCell(1)
                val description = descriptionCell?.stringCellValue?.trim() ?: ""

                val ingredientsCell = row.getCell(2)
                val ingredients = ingredientsCell?.stringCellValue
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?: emptyList()

                val instructionsCell = row.getCell(3)
                val instructions = instructionsCell?.stringCellValue
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?: emptyList()

                val cookingTimeCell = row.getCell(4)
                val cookingTime = when (cookingTimeCell?.cellType) {
                    CellType.NUMERIC -> cookingTimeCell.numericCellValue.toInt()
                    CellType.STRING -> cookingTimeCell.stringCellValue.toIntOrNull() ?: 0
                    else -> 0
                }

                val caloriesCell = row.getCell(5)
                val calories = when (caloriesCell?.cellType) {
                    CellType.NUMERIC -> caloriesCell.numericCellValue.toInt()
                    CellType.STRING -> caloriesCell.stringCellValue.toIntOrNull() ?: 0
                    else -> 0
                }

                val servingsCell = row.getCell(6)
                val servings = when (servingsCell?.cellType) {
                    CellType.NUMERIC -> servingsCell.numericCellValue.toInt()
                    CellType.STRING -> servingsCell.stringCellValue.toIntOrNull() ?: 1
                    else -> 1
                }

                val isBreakfastCell = row.getCell(7)
                val isBreakfast = when {
                    isBreakfastCell?.cellType == CellType.BOOLEAN -> isBreakfastCell.getBooleanCellValue()
                    isBreakfastCell?.cellType == CellType.STRING -> isBreakfastCell.stringCellValue.lowercase() == "true"
                    else -> false
                }

                val isLunchCell = row.getCell(8)
                val isLunch = when {
                    isLunchCell?.cellType == CellType.BOOLEAN -> isLunchCell.getBooleanCellValue()
                    isLunchCell?.cellType == CellType.STRING -> isLunchCell.stringCellValue.lowercase() == "true"
                    else -> false
                }

                val isDinnerCell = row.getCell(9)
                val isDinner = when {
                    isDinnerCell?.cellType == CellType.BOOLEAN -> isDinnerCell.getBooleanCellValue()
                    isDinnerCell?.cellType == CellType.STRING -> isDinnerCell.stringCellValue.lowercase() == "true"
                    else -> false
                }

                val isSnackCell = row.getCell(10)
                val isSnack = when {
                    isSnackCell?.cellType == CellType.BOOLEAN -> isSnackCell.getBooleanCellValue()
                    isSnackCell?.cellType == CellType.STRING -> isSnackCell.stringCellValue.lowercase() == "true"
                    else -> false
                }

                Log.d(TAG, """
                    Parsing recipe:
                    Name: $name
                    Description: $description
                    Ingredients: ${ingredients.joinToString(", ")}
                    Instructions: ${instructions.joinToString(", ")}
                    Time: $cookingTime
                    Calories: $calories
                    Servings: $servings
                    Types: B:$isBreakfast L:$isLunch D:$isDinner S:$isSnack
                """.trimIndent())

                if (ingredients.isEmpty() || instructions.isEmpty()) {
                    Log.d(TAG, "Skipping recipe $name: missing ingredients or instructions")
                    continue
                }

                val recipe = Recipe(
                    name = name,
                    description = description,
                    cookingTime = cookingTime,
                    calories = calories,
                    servings = servings,
                    ingredients = ingredients,
                    instructions = instructions,
                    isBreakfast = isBreakfast,
                    isLunch = isLunch,
                    isDinner = isDinner,
                    isSnack = isSnack
                )

                recipes.add(recipe)
                Log.d(TAG, "Added recipe: $name with ${ingredients.size} ingredients and ${instructions.size} instructions")
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing row $rowIndex", e)
            }
        }

        workbook.close()
        return recipes
    }
} 