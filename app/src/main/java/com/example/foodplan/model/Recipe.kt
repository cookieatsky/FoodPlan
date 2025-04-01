package com.example.foodplan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Long = 0,
    val name: String,
    val description: String,
    val imageUri: String?,
    val cookingTime: Int, // в минутах
    val calories: Int,
    val servings: Int,
    val ingredients: List<String>,
    val instructions: List<String>,
    val isBreakfast: Boolean = false,
    val isLunch: Boolean = false,
    val isDinner: Boolean = false,
    val isSnack: Boolean = false
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (imageUri != other.imageUri) return false
        if (cookingTime != other.cookingTime) return false
        if (calories != other.calories) return false
        if (servings != other.servings) return false
        if (ingredients != other.ingredients) return false
        if (instructions != other.instructions) return false
        if (isBreakfast != other.isBreakfast) return false
        if (isLunch != other.isLunch) return false
        if (isDinner != other.isDinner) return false
        if (isSnack != other.isSnack) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (imageUri?.hashCode() ?: 0)
        result = 31 * result + cookingTime
        result = 31 * result + calories
        result = 31 * result + servings
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + isBreakfast.hashCode()
        result = 31 * result + isLunch.hashCode()
        result = 31 * result + isDinner.hashCode()
        result = 31 * result + isSnack.hashCode()
        return result
    }
} 