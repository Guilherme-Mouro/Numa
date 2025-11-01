package com.example.numa.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep")
data class Sleep (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,
    val date: Long,
    val duration: Long,
    val timesAwake: Int,
    val score: Double,
    val points: Int,
    val experience: Int,

)