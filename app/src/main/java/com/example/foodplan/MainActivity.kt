package com.example.foodplan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplan.ui.recipes.RecipesActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация кнопок
        val recipesButton = findViewById<MaterialButton>(R.id.recipesButton)
        val mealPlanButton = findViewById<MaterialButton>(R.id.mealPlanButton)
        val shoppingListButton = findViewById<MaterialButton>(R.id.shoppingListButton)
        val profileButton = findViewById<MaterialButton>(R.id.profileButton)

        // Обработчики нажатий
        recipesButton.setOnClickListener {
            startActivity(Intent(this, RecipesActivity::class.java))
        }

        mealPlanButton.setOnClickListener {
            startActivity(Intent(this, MealPlanActivity::class.java))
        }

        shoppingListButton.setOnClickListener {
            // TODO: Переход к экрану списка покупок
        }

        profileButton.setOnClickListener {
            // TODO: Переход к экрану профиля
        }
    }
}