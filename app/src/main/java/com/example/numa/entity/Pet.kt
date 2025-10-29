package com.example.numa.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "pet", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
])

data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,
    val name: String,
    val level: Int,
    val humor: String,
    val sprite: String,

    )