package com.example.numa.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievement_user",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Achievement::class,
            parentColumns = ["id"],
            childColumns = ["achievementId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AchievementUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,
    val achievementId: Int,
    val unlockedDate: Long = System.currentTimeMillis(),
    val isUnlocked: Boolean = true,
)