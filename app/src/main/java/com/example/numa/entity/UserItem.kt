package com.example.numa.entity

import androidx.room.Entity

@Entity(
    tableName = "userItem",
    primaryKeys = ["userId", "itemId"]
)
data class UserItem(
    val userId: Int,
    val itemId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
