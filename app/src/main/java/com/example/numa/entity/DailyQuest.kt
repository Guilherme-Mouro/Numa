package com.example.numa.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_quest")
data class DailyQuest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val type: String,        // Ex: "SLEEP", "HABIT", "SHOP", "CREATE_HABIT"
    val description: String, // O texto que aparece na UI
    val target: Int,         // Quantas vezes precisa fazer (ex: 1, 2)
    val progress: Int = 0,   // Progresso atual
    val date: Long,          // Timestamp do dia em que foi criada (para o reset)
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false // Se já recebeu a recompensa (opcional)
)