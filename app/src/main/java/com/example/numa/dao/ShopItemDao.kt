package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.ShopItem

@Dao
interface ShopItemDao {

    @Query("SELECT * FROM shopItem")
    suspend fun getAllShopItem(): List<ShopItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shopItems: List<ShopItem>)

    @Delete
    suspend fun deleteShopItem(shopItem: ShopItem)
}
