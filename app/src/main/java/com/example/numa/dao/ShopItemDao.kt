package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.ShopItem

@Dao
interface ShopItemDao {

    @Query("SELECT * FROM shopItem")
    suspend fun getAllShopItem(): List<ShopItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ShopItem>)

    @Query("DELETE FROM ShopItem")
    suspend fun deleteAll()
}
