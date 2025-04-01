package com.example.foodplan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class MealPlan(
    val date: LocalDate,
    val breakfastRecipes: MutableList<Recipe> = mutableListOf(),
    val lunchRecipes: MutableList<Recipe> = mutableListOf(),
    val dinnerRecipes: MutableList<Recipe> = mutableListOf(),
    val snackRecipes: MutableList<Recipe> = mutableListOf()
) : Parcelable

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
} 