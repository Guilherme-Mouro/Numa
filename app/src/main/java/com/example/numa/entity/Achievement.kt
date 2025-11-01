package com.example.numa.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "achievement", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Achievement (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,
    val title: String,
    val date: Long,
    val points: Int,
    val experience: Int,
)