package com.example.foodplan.ui.recipes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodplan.databinding.ActivityImportRecipesBinding
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ImportRecipesActivity : AppCompatActivity() {
    private val TAG = "ImportRecipesActivity"
    private lateinit var binding: ActivityImportRecipesBinding
    private lateinit var recipeRepository: RecipeRepository

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

        recipeRepository = RecipeRepository.getInstance(this)

        binding.selectFileButton.setOnClickListener {
            getContent.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
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
                    
                    parsedRecipes.forEach { recipe ->
                        Log.d(TAG, "Saving recipe: ${recipe.name} with ${recipe.ingredients.size} ingredients and ${recipe.instructions.size} instructions")
                        recipeRepository.insertRecipe(recipe)
                    }
                    parsedRecipes
                }

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
                Log.d(TAG, "Processing recipe: $name at row $rowIndex")

                val description = row.getCell(1)?.stringCellValue?.trim() ?: ""
                
                // Получаем и проверяем ингредиенты
                val ingredientsCell = row.getCell(2)
                val ingredients = if (ingredientsCell != null && ingredientsCell.stringCellValue.isNotBlank()) {
                    val ingredientsStr = ingredientsCell.stringCellValue
                    Log.d(TAG, "Raw ingredients for $name: $ingredientsStr")
                    ingredientsStr.split(";").map { it.trim() }.filter { it.isNotEmpty() }
                } else {
                    Log.d(TAG, "No ingredients found for $name")
                    emptyList()
                }
                
                // Получаем и проверяем инструкции
                val instructionsCell = row.getCell(3)
                val instructions = if (instructionsCell != null && instructionsCell.stringCellValue.isNotBlank()) {
                    val instructionsStr = instructionsCell.stringCellValue
                    Log.d(TAG, "Raw instructions for $name: $instructionsStr")
                    instructionsStr.split(";").map { it.trim() }.filter { it.isNotEmpty() }
                } else {
                    Log.d(TAG, "No instructions found for $name")
                    emptyList()
                }
                
                // Преобразуем числовые значения
                val cookingTime = when (val cell = row.getCell(4)) {
                    null -> 0
                    else -> {
                        when (cell.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue.toInt()
                            CellType.STRING -> cell.stringCellValue.toIntOrNull() ?: 0
                            else -> 0
                        }
                    }
                }
                
                val calories = when (val cell = row.getCell(5)) {
                    null -> 0
                    else -> {
                        when (cell.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue.toInt()
                            CellType.STRING -> cell.stringCellValue.toIntOrNull() ?: 0
                            else -> 0
                        }
                    }
                }
                
                val servings = when (val cell = row.getCell(6)) {
                    null -> 1
                    else -> {
                        when (cell.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue.toInt()
                            CellType.STRING -> cell.stringCellValue.toIntOrNull() ?: 1
                            else -> 1
                        }
                    }
                }

                // Преобразуем булевы значения
                val isBreakfast = row.getCell(7)?.stringCellValue?.lowercase() == "true"
                val isLunch = row.getCell(8)?.stringCellValue?.lowercase() == "true"
                val isDinner = row.getCell(9)?.stringCellValue?.lowercase() == "true"
                val isSnack = row.getCell(10)?.stringCellValue?.lowercase() == "true"

                val recipe = Recipe(
                    name = name,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    cookingTime = cookingTime,
                    calories = calories,
                    servings = servings,
                    isBreakfast = isBreakfast,
                    isLunch = isLunch,
                    isDinner = isDinner,
                    isSnack = isSnack
                )
                
                Log.d(TAG, "Created recipe: $name with ${ingredients.size} ingredients and ${instructions.size} instructions")
                recipes.add(recipe)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing row $rowIndex", e)
                e.printStackTrace()
            }
        }

        workbook.close()
        Log.d(TAG, "Finished parsing Excel file. Found ${recipes.size} recipes")
        return recipes
    }
} 