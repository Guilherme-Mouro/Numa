package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.AchievementUser
import com.example.numa.entity.Achievement

@Dao
interface AchievementUserDao {

    @Query("SELECT * FROM achievement_user WHERE userId = :userId")
    suspend fun getAchievementsByUser(userId: Int): List<AchievementUser>

    @Query("SELECT COUNT(*) FROM achievement_user WHERE userId = :userId AND isUnlocked = 1")
    suspend fun countUnlockedByUser(userId: Int): Int

    @Query("SELECT * FROM achievement_user WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun getUserAchievement(userId: Int, achievementId: Int): AchievementUser?

    @Query("""
        SELECT a.* FROM achievement a
        INNER JOIN achievement_user au ON a.id = au.achievementId
        WHERE au.userId = :userId AND au.isUnlocked = 1
        ORDER BY au.unlockedDate DESC
    """)
    suspend fun getUnlockedAchievementsForUser(userId: Int): List<Achievement>

    @Query("""
        SELECT a.* FROM achievement a
        WHERE a.id NOT IN (
            SELECT achievementId FROM achievement_user 
            WHERE userId = :userId AND isUnlocked = 1
        )
    """)
    suspend fun getLockedAchievementsForUser(userId: Int): List<Achievement>

    @Insert
    suspend fun insertAchievementUser(achievementUser: AchievementUser): Long

    @Update
    suspend fun updateAchievementUser(achievementUser: AchievementUser)

    @Delete
    suspend fun deleteAchievementUser(achievementUser: AchievementUser)
}
