package com.example.numa.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopItem")
data class ShopItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val item: String,
)
