package com.example.foodplan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val id: Long = 0,
    val name: String,
    val age: Int,
    val weight: Float,
    val height: Int,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    val dailyCaloriesGoal: Int
) : Parcelable

@Parcelize
enum class Gender : Parcelable {
    MALE, FEMALE, OTHER;

    override fun describeContents(): Int = 0
}

@Parcelize
enum class ActivityLevel : Parcelable {
    SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE;

    override fun describeContents(): Int = 0
} 