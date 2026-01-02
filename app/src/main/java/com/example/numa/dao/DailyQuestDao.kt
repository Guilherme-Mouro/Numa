package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.DailyQuest

@Dao
interface DailyQuestDao {
    @Query("SELECT * FROM daily_quest WHERE userId = :userId")
    suspend fun getQuestsByUser(userId: Int): List<DailyQuest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<DailyQuest>)

    @Query("DELETE FROM daily_quest WHERE userId = :userId")
    suspend fun clearQuestsForUser(userId: Int)

    @Query("UPDATE daily_quest SET progress = :progress, isCompleted = :isCompleted WHERE id = :questId")
    suspend fun updateProgress(questId: Int, progress: Int, isCompleted: Boolean)

    // Busca missões de um tipo específico (ex: "HABIT")
    @Query("SELECT * FROM daily_quest WHERE userId = :userId AND type = :type AND isCompleted = 0")
    suspend fun getActiveQuestsByType(userId: Int, type: String): List<DailyQuest>
}