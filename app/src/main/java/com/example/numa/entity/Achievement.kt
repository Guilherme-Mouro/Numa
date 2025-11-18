package com.example.numa.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "achievement")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val type: String,                 // "DAILY_STREAK", "HABIT_STREAK", "COLECIONADOR", "META_CHAMPION"
    val level: Int,                   // 1, 3, 7, 10, 30, 50, 100, 365 (o "quanto")
    val title: String,                // "Semana Ativa"
    val description: String,          // "Completar 7 dias seguidos com ≥1 hábito"
    val points: Int,                  // Pontos que ganha
    val experience: Int,              // Experiência que ganha
)
