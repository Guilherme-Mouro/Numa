package com.example.numa.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep")
data class Sleep (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,
    val date: Long,

    //val duration: Long,
    val startTime: Long,
    val endTime: Long,

    val timesAwake: Int,

    val snoring: Boolean,
    val snoringAudioPath: String?,

    val noiseLevel: Double,

    val score: Double,
    val quality: String,
    val points: Int,
    val experience: Int,
)