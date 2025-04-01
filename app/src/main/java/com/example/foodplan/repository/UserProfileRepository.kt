package com.example.foodplan.repository

import android.content.Context
import com.example.foodplan.data.AppDatabase
import com.example.foodplan.data.UserProfileDao
import com.example.foodplan.data.UserProfileEntity
import com.example.foodplan.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepository private constructor(context: Context) {
    private val userProfileDao: UserProfileDao = AppDatabase.getInstance(context).userProfileDao()

    fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.toUserProfile()
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        try {
            userProfileDao.insertUserProfile(UserProfileEntity.fromUserProfile(profile))
        } catch (e: Exception) {
            // В случае ошибки, попробуем обновить существующий профиль
            userProfileDao.updateUserProfile(UserProfileEntity.fromUserProfile(profile))
        }
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        val entity = UserProfileEntity.fromUserProfile(profile)
        userProfileDao.updateUserProfile(entity)
    }

    companion object {
        @Volatile
        private var INSTANCE: UserProfileRepository? = null

        fun getInstance(context: Context): UserProfileRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserProfileRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
} 