package com.example.foodplan.ui.mealplan

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.adapter.DateAdapter
import com.example.foodplan.adapter.MealRecipeAdapter
import com.example.foodplan.adapter.SelectRecipeAdapter
import com.example.foodplan.databinding.ActivityMealPlanBinding
import com.example.foodplan.model.MealPlan
import com.example.foodplan.model.Recipe
import com.example.foodplan.model.ShoppingListItem
import com.example.foodplan.repository.MealPlanRepository
import com.example.foodplan.repository.RecipeRepository
import com.example.foodplan.repository.ShoppingListRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class MealPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealPlanBinding
    private lateinit var dateAdapter: DateAdapter
    private lateinit var breakfastAdapter: MealRecipeAdapter
    private lateinit var lunchAdapter: MealRecipeAdapter
    private lateinit var dinnerAdapter: MealRecipeAdapter
    private lateinit var snackAdapter: MealRecipeAdapter
    private lateinit var selectRecipeAdapter: SelectRecipeAdapter

    private var currentDate = LocalDate.now()
    private var mealPlan = MealPlan(currentDate)
    private var currentMealType: MealType = MealType.BREAKFAST

    private lateinit var mealPlanRepository: MealPlanRepository
    private lateinit var recipeRepository: RecipeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeRepository = RecipeRepository.getInstance(this)
        mealPlanRepository = MealPlanRepository.getInstance(this)

        setupDateRecyclerView()
        setupMealRecyclerViews()
        setupButtons()
        loadMealPlan()
    }

    private fun setupDateRecyclerView() {
        dateAdapter = DateAdapter { date ->
            currentDate = date
            loadMealPlan()
        }

        binding.dateRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
        }

        // Генерируем даты на неделю вперед
        val dates = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
        dateAdapter.submitList(dates)
    }

    private fun setupMealRecyclerViews() {
        breakfastAdapter = MealRecipeAdapter { recipe ->
            mealPlan.breakfastRecipes.remove(recipe)
            saveMealPlan()
        }

        lunchAdapter = MealRecipeAdapter { recipe ->
            mealPlan.lunchRecipes.remove(recipe)
            saveMealPlan()
        }

        dinnerAdapter = MealRecipeAdapter { recipe ->
            mealPlan.dinnerRecipes.remove(recipe)
            saveMealPlan()
        }

        snackAdapter = MealRecipeAdapter { recipe ->
            mealPlan.snackRecipes.remove(recipe)
            saveMealPlan()
        }

        selectRecipeAdapter = SelectRecipeAdapter { recipe ->
            addRecipeToMeal(recipe)
        }

        binding.breakfastRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = breakfastAdapter
        }

        binding.lunchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = lunchAdapter
        }

        binding.dinnerRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = dinnerAdapter
        }

        binding.snackRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = snackAdapter
        }
    }

    private fun setupButtons() {
        binding.addBreakfastButton.setOnClickListener {
            currentMealType = MealType.BREAKFAST
            showSelectRecipeDialog(MealType.BREAKFAST)
        }

        binding.addLunchButton.setOnClickListener {
            currentMealType = MealType.LUNCH
            showSelectRecipeDialog(MealType.LUNCH)
        }

        binding.addDinnerButton.setOnClickListener {
            currentMealType = MealType.DINNER
            showSelectRecipeDialog(MealType.DINNER)
        }

        binding.addSnackButton.setOnClickListener {
            currentMealType = MealType.SNACK
            showSelectRecipeDialog(MealType.SNACK)
        }
    }

    private fun showSelectRecipeDialog(mealType: MealType) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_recipe, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = selectRecipeAdapter

        lifecycleScope.launch {
            recipeRepository.getAllRecipes().collectLatest { recipes ->
                selectRecipeAdapter.submitList(recipes.filter { recipe ->
                    when (mealType) {
                        MealType.BREAKFAST -> recipe.isBreakfast
                        MealType.LUNCH -> recipe.isLunch
                        MealType.DINNER -> recipe.isDinner
                        MealType.SNACK -> recipe.isSnack
                    }
                })
            }
        }

        AlertDialog.Builder(this)
            .setTitle(when (mealType) {
                MealType.BREAKFAST -> "Выберите рецепт для завтрака"
                MealType.LUNCH -> "Выберите рецепт для обеда"
                MealType.DINNER -> "Выберите рецепт для ужина"
                MealType.SNACK -> "Выберите рецепт для перекуса"
            })
            .setView(dialogView)
            .setPositiveButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addRecipeToMeal(recipe: Recipe) {
        when (currentMealType) {
            MealType.BREAKFAST -> mealPlan.breakfastRecipes.add(recipe)
            MealType.LUNCH -> mealPlan.lunchRecipes.add(recipe)
            MealType.DINNER -> mealPlan.dinnerRecipes.add(recipe)
            MealType.SNACK -> mealPlan.snackRecipes.add(recipe)
        }
        saveMealPlan()
        addIngredientsToShoppingList(recipe)
    }

    private fun addIngredientsToShoppingList(recipe: Recipe) {
        val date = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val shoppingItems = recipe.ingredients.map { ingredient ->
            ShoppingListItem(
                name = ingredient,
                date = date,
                recipeId = recipe.id
            )
        }
        lifecycleScope.launch {
            ShoppingListRepository.getInstance(this@MealPlanActivity).addItems(shoppingItems)
        }
    }

    private fun loadMealPlan() {
        lifecycleScope.launch {
            mealPlan = mealPlanRepository.getMealPlan(currentDate)
            updateUI()
        }
    }

    private fun saveMealPlan() {
        lifecycleScope.launch {
            mealPlanRepository.saveMealPlan(mealPlan)
            updateUI()
        }
    }

    private fun updateUI() {
        breakfastAdapter.submitList(mealPlan.breakfastRecipes.toList())
        lunchAdapter.submitList(mealPlan.lunchRecipes.toList())
        dinnerAdapter.submitList(mealPlan.dinnerRecipes.toList())
        snackAdapter.submitList(mealPlan.snackRecipes.toList())
        
        // Обновляем текст выбранной даты
        binding.selectedDateTextView.text = currentDate.toString()
    }
}

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
} 