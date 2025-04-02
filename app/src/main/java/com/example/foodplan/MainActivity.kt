package com.example.foodplan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplan.databinding.ActivityMainBinding
import com.example.foodplan.ui.mealplan.MealPlanActivity
import com.example.foodplan.ui.profile.ProfileActivity
import com.example.foodplan.ui.recipes.RecipesActivity
import com.example.foodplan.ui.shoppinglist.ShoppingListActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация кнопок
        val recipesButton = binding.recipesButton
        val mealPlanButton = binding.mealPlanButton
        val shoppingListButton = binding.shoppingListButton
        val profileButton = binding.profileButton

        // Обработчики нажатий
        recipesButton.setOnClickListener {
            startActivity(Intent(this, RecipesActivity::class.java))
        }

        mealPlanButton.setOnClickListener {
            startActivity(Intent(this, MealPlanActivity::class.java))
        }

        shoppingListButton.setOnClickListener {
            startActivity(Intent(this, ShoppingListActivity::class.java))
        }

        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}