package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Habit
import com.example.numa.entity.Pet
import com.example.numa.entity.ShopItem
import com.example.numa.entity.UserItem

@Dao
interface UserItemDao {
    @Query("SELECT * FROM userItem WHERE userId = :userId")
    suspend fun getUserItemByUserId(userId: Int): List<UserItem>

    @Query("SELECT * FROM userItem WHERE userId = :userId AND itemId = :itemId")
    suspend fun getUserItemByIds(userId: Int, itemId: Int): UserItem?

    @Query("SELECT * FROM userItem")
    suspend fun getAllUserItems(): List<UserItem>

    @Insert
    suspend fun insertUserItem(userItem: UserItem): Long
}
