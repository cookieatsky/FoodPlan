package com.example.foodplan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingListItem(
    val id: Long = 0,
    val name: String,
    val isChecked: Boolean = false,
    val date: Long,
    val recipeId: Long? = null
) : Parcelable 