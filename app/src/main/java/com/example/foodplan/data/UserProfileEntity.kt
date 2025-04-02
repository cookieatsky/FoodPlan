package com.example.foodplan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodplan.model.ActivityLevel
import com.example.foodplan.model.Gender
import com.example.foodplan.model.UserProfile

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val weight: Float,
    val height: Int,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    val dailyCaloriesGoal: Int
) {
    fun toUserProfile(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            age = age,
            weight = weight,
            height = height,
            gender = gender,
            activityLevel = activityLevel,
            dailyCaloriesGoal = dailyCaloriesGoal
        )
    }

    companion object {
        fun fromUserProfile(profile: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                id = profile.id,
                name = profile.name,
                age = profile.age,
                weight = profile.weight,
                height = profile.height,
                gender = profile.gender,
                activityLevel = profile.activityLevel,
                dailyCaloriesGoal = profile.dailyCaloriesGoal
            )
        }
    }
} 