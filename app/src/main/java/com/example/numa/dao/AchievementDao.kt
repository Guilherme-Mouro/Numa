package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Achievement

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievement WHERE userId = :userId")
    suspend fun getAchievementsByUser(userId: Int): List<Achievement>

    @Query("SELECT * FROM achievement WHERE id = :achievementId")
    suspend fun getAchievementById(achievementId: Int): Achievement?

    @Query("UPDATE achievement SET title = :title, date = :date, points = :points, experience = :experience WHERE id = :achievementId")
    suspend fun updateAchievement(
        achievementId: Int,
        title: String,
        date: Long,
        points: Int,
        experience: Int
    )

    @Insert
    suspend fun insertAchievement(achievement: Achievement): Long

    @Delete
    suspend fun deleteAchievement(achievement: Achievement)
}
