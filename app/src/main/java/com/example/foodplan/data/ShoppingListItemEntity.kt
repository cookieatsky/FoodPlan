package com.example.foodplan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodplan.model.ShoppingListItem

@Entity(tableName = "shopping_list_items")
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isChecked: Boolean = false,
    val date: Long,
    val recipeId: Long? = null
) {
    fun toShoppingListItem(): ShoppingListItem {
        return ShoppingListItem(
            id = id,
            name = name,
            isChecked = isChecked,
            date = date,
            recipeId = recipeId
        )
    }

    companion object {
        fun fromShoppingListItem(item: ShoppingListItem): ShoppingListItemEntity {
            return ShoppingListItemEntity(
                id = item.id,
                name = item.name,
                isChecked = item.isChecked,
                date = item.date,
                recipeId = item.recipeId
            )
        }
    }
} 