package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Achievement

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievement")
    suspend fun getAllAchievements(): List<Achievement>

    @Query("SELECT * FROM achievement WHERE type = :type AND level = :level")
    suspend fun getAchievementByTypeAndLevel(type: String, level: Int): Achievement?

    @Query("SELECT * FROM achievement WHERE type = :type")
    suspend fun getAchievementsByType(type: String): List<Achievement>?

    @Query("SELECT * FROM achievement WHERE id = :id")
    suspend fun getAchievementById(id: Int): Achievement?

    @Insert
    suspend fun insertAchievement(achievement: Achievement): Long

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Delete
    suspend fun deleteAchievement(achievement: Achievement)
}

