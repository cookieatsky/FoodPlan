package com.example.foodplan.repository

import android.content.Context
import com.example.foodplan.data.AppDatabase
import com.example.foodplan.data.ShoppingListItemDao
import com.example.foodplan.data.ShoppingListItemEntity
import com.example.foodplan.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShoppingListRepository private constructor(context: Context) {
    private val shoppingListDao: ShoppingListItemDao = AppDatabase.getInstance(context).shoppingListDao()

    fun getAllItems(): Flow<List<ShoppingListItem>> {
        return shoppingListDao.getAllItems().map { entities ->
            entities.map { it.toShoppingListItem() }
        }
    }

    fun getItemsByDate(date: Long): Flow<List<ShoppingListItem>> {
        return shoppingListDao.getItemsByDate(date).map { entities ->
            entities.map { it.toShoppingListItem() }
        }
    }

    suspend fun addItems(items: List<ShoppingListItem>) {
        shoppingListDao.insertItems(items.map { ShoppingListItemEntity.fromShoppingListItem(it) })
    }

    suspend fun updateItem(item: ShoppingListItem) {
        shoppingListDao.updateItem(ShoppingListItemEntity.fromShoppingListItem(item))
    }

    suspend fun deleteItem(item: ShoppingListItem) {
        shoppingListDao.deleteItem(ShoppingListItemEntity.fromShoppingListItem(item))
    }

    suspend fun deleteItemsBeforeDate(date: Long) {
        shoppingListDao.deleteItemsBeforeDate(date)
    }

    companion object {
        @Volatile
        private var INSTANCE: ShoppingListRepository? = null

        fun getInstance(context: Context): ShoppingListRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ShoppingListRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
} 