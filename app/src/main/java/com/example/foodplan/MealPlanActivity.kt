package com.example.foodplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.foodplan.adapter.DateAdapter
import com.example.foodplan.adapter.MealRecipeAdapter
import com.example.foodplan.adapter.SelectRecipeAdapter
import com.example.foodplan.databinding.ActivityMealPlanBinding
import com.example.foodplan.model.MealPlan
import com.example.foodplan.model.MealType
import com.example.foodplan.model.Recipe
import com.example.foodplan.repository.MealPlanRepository
import com.example.foodplan.repository.RecipeRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MealPlanActivity : AppCompatActivity() {

    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var selectedDateTextView: TextView
    private lateinit var breakfastRecyclerView: RecyclerView
    private lateinit var lunchRecyclerView: RecyclerView
    private lateinit var dinnerRecyclerView: RecyclerView
    private lateinit var snackRecyclerView: RecyclerView

    private lateinit var addBreakfastButton: MaterialButton
    private lateinit var addLunchButton: MaterialButton
    private lateinit var addDinnerButton: MaterialButton
    private lateinit var addSnackButton: MaterialButton

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

    private lateinit var binding: ActivityMealPlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeRepository = RecipeRepository.getInstance(this)
        mealPlanRepository = MealPlanRepository.getInstance()

        setupDateRecyclerView()
        setupMealRecyclerView()
        loadMealPlan()
    }

    private fun setupDateRecyclerView() {
        dateAdapter = DateAdapter { date ->
            currentDate = date
            loadMealPlan()
        }

        dateRecyclerView = binding.dateRecyclerView
        dateRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
        }

        // Генерируем даты на неделю вперед..
        val dates = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
        dateAdapter.submitList(dates)
    }

    private fun setupMealRecyclerView() {
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

        breakfastRecyclerView = binding.breakfastRecyclerView
        breakfastRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = breakfastAdapter
        }

        lunchRecyclerView = binding.lunchRecyclerView
        lunchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = lunchAdapter
        }

        dinnerRecyclerView = binding.dinnerRecyclerView
        dinnerRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = dinnerAdapter
        }

        snackRecyclerView = binding.snackRecyclerView
        snackRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanActivity)
            adapter = snackAdapter
        }

        setupButtons()
    }

    private fun setupButtons() {
        addBreakfastButton = binding.addBreakfastButton
        addLunchButton = binding.addLunchButton
        addDinnerButton = binding.addDinnerButton
        addSnackButton = binding.addSnackButton

        addBreakfastButton.setOnClickListener {
            currentMealType = MealType.BREAKFAST
            showSelectRecipeDialog(MealType.BREAKFAST)
        }

        addLunchButton.setOnClickListener {
            currentMealType = MealType.LUNCH
            showSelectRecipeDialog(MealType.LUNCH)
        }

        addDinnerButton.setOnClickListener {
            currentMealType = MealType.DINNER
            showSelectRecipeDialog(MealType.DINNER)
        }

        addSnackButton.setOnClickListener {
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
        updateUI()
    }

    private fun loadMealPlan() {
        lifecycleScope.launch {
            mealPlanRepository.getMealPlan(currentDate).collectLatest { plan ->
                mealPlan = plan ?: MealPlan(currentDate)
                updateUI()
            }
        }
    }

    private fun saveMealPlan() {
        lifecycleScope.launch {
            mealPlanRepository.saveMealPlan(mealPlan)
        }
    }

    private fun updateUI() {
        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
        selectedDateTextView = binding.selectedDateTextView
        selectedDateTextView.text = currentDate.format(dateFormatter)

        breakfastAdapter.submitList(mealPlan.breakfastRecipes)
        lunchAdapter.submitList(mealPlan.lunchRecipes)
        dinnerAdapter.submitList(mealPlan.dinnerRecipes)
        snackAdapter.submitList(mealPlan.snackRecipes)
    }
} 