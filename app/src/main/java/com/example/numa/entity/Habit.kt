package com.example.numa.entity

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

    val userId: Int,
    val title: String,
    val description: String,
    val startTime: Long,
    val duration: Long,
    val experience: Int,
    val streak: Int,
    val state: String,

    val isRecurring: Boolean,
    val dayOfWeek: String? = null,
    val specificDate: Long? = null,

    val type: String,

)