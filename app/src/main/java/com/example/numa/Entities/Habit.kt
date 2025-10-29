package com.example.numa.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "habit", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
])

data class Habit (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userID: Int,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val state: String,

    )