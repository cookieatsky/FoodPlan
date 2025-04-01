package com.example.foodplan.repository

import com.example.foodplan.model.MealPlan
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MealPlanRepository private constructor() {
    private val mealPlans = MutableStateFlow<Map<LocalDate, MealPlan>>(emptyMap())

    fun getMealPlan(date: LocalDate): Flow<MealPlan?> {
        return mealPlans.map { plans -> plans[date] }
    }

    suspend fun saveMealPlan(mealPlan: MealPlan) {
        val currentPlans = mealPlans.value.toMutableMap()
        currentPlans[mealPlan.date] = mealPlan
        mealPlans.value = currentPlans
    }

    suspend fun deleteMealPlan(date: LocalDate) {
        val currentPlans = mealPlans.value.toMutableMap()
        currentPlans.remove(date)
        mealPlans.value = currentPlans
    }

    fun getAllMealPlans(): Flow<List<MealPlan>> {
        return mealPlans.map { plans -> plans.values.toList() }
    }

    companion object {
        @Volatile
        private var INSTANCE: MealPlanRepository? = null

        fun getInstance(): MealPlanRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MealPlanRepository()
                INSTANCE = instance
                instance
            }
        }
    }
} 