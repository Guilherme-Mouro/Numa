package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.UserItem

@Dao
interface UserItemDao {

    @Query("SELECT * FROM shopItem")
    suspend fun getAllShopItem(): List<UserItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shopItems: List<UserItem>)

    @Delete
    suspend fun deleteShopItem(shopItem: UserItem)
}
