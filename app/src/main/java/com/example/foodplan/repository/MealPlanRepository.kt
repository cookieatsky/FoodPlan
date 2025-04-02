package com.example.foodplan.repository

import android.content.Context
import com.example.foodplan.model.MealPlan
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MealPlanRepository private constructor(private val context: Context) {
    private val _mealPlans = MutableStateFlow<Map<LocalDate, MealPlan>>(emptyMap())
    val mealPlans: Flow<Map<LocalDate, MealPlan>> = _mealPlans.asStateFlow()

    suspend fun getMealPlan(date: LocalDate): MealPlan {
        return _mealPlans.value[date] ?: MealPlan(date)
    }

    suspend fun saveMealPlan(mealPlan: MealPlan) {
        val currentPlans = _mealPlans.value.toMutableMap()
        currentPlans[mealPlan.date] = mealPlan
        _mealPlans.value = currentPlans
    }

    suspend fun deleteMealPlan(date: LocalDate) {
        val currentPlans = _mealPlans.value.toMutableMap()
        currentPlans.remove(date)
        _mealPlans.value = currentPlans
    }

    fun getAllMealPlans(): Flow<List<MealPlan>> {
        return _mealPlans.map { plans -> plans.values.toList() }
    }

    companion object {
        @Volatile
        private var INSTANCE: MealPlanRepository? = null

        fun getInstance(context: Context): MealPlanRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MealPlanRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
} 