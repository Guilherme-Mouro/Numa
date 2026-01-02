package com.example.numa

import com.example.numa.dao.AchievementDao
import com.example.numa.dao.AchievementUserDao
import com.example.numa.dao.UserDao
import com.example.numa.dao.HabitDao
import com.example.numa.dao.SleepDao
import com.example.numa.entity.AchievementUser
import com.example.numa.util.UserRepository

class CheckAchievementRepository(
    private val achievementDao: AchievementDao,
    private val achievementUserDao: AchievementUserDao,
    private val userDao: UserDao,
    private val habitDao: HabitDao,
    private val sleepDao: SleepDao
) {

    // Inicializa o UserRepository aqui usando o userDao existente
    private val userRepository = UserRepository(userDao)

    suspend fun checkAndUnlockAchievement(userId: Int, type: String, level: Int) {
        val achievement = achievementDao.getAchievementByTypeAndLevel(type, level) ?: return

        // Verifica se user já tem este achievement
        val existing = achievementUserDao.getUserAchievement(userId, achievement.id)

        if (existing == null) {
            // Não tem - desbloqueia!
            achievementUserDao.insertAchievementUser(
                AchievementUser(
                    userId = userId,
                    achievementId = achievement.id,
                    isUnlocked = true,
                    unlockedDate = System.currentTimeMillis()
                )
            )

            // Usa a função do UserRepository para dar XP e Pontos
            userRepository.addXpAndPoints(
                userId = userId,
                xpEarned = achievement.experience,
                pointsEarned = achievement.points
            )
        }
    }

    suspend fun checkAllAchievements(userId: Int, habitId: Int? = null) {
        val user = userDao.getUserById(userId) ?: return
        val habits = habitDao.getHabitsByUser(userId)

        // DAILY STREAK
        if (user.streak >= 1) checkAndUnlockAchievement(userId, "DAILY_STREAK", 1)
        if (user.streak >= 7) checkAndUnlockAchievement(userId, "DAILY_STREAK", 7)
        if (user.streak >= 30) checkAndUnlockAchievement(userId, "DAILY_STREAK", 30)
        if (user.streak >= 90) checkAndUnlockAchievement(userId, "DAILY_STREAK", 90)
        if (user.streak >= 365) checkAndUnlockAchievement(userId, "DAILY_STREAK", 365)

        // HABIT STREAK
        if (habitId != null) {
            val habit = habitDao.getHabitById(habitId)

            habit?.let {
                if (it.streak >= 2) checkAndUnlockAchievement(userId, "HABIT_STREAK", 2)
                if (it.streak >= 7) checkAndUnlockAchievement(userId, "HABIT_STREAK", 7)
                if (it.streak >= 30) checkAndUnlockAchievement(userId, "HABIT_STREAK", 30)
                if (it.streak >= 60) checkAndUnlockAchievement(userId, "HABIT_STREAK", 60)
                if (it.streak >= 100) checkAndUnlockAchievement(userId, "HABIT_STREAK", 100)
                if (it.streak >= 365) checkAndUnlockAchievement(userId, "HABIT_STREAK", 365)
            }
        }

        // COLECIONADOR
        if (habits.size >= 1) checkAndUnlockAchievement(userId, "COLECIONADOR", 1)
        if (habits.size >= 3) checkAndUnlockAchievement(userId, "COLECIONADOR", 3)
        if (habits.size >= 10) checkAndUnlockAchievement(userId, "COLECIONADOR", 10)
        if (habits.size >= 20) checkAndUnlockAchievement(userId, "COLECIONADOR", 20)
        if (habits.size >= 50) checkAndUnlockAchievement(userId, "COLECIONADOR", 50)

        // SLEEP
        val sleepCount = sleepDao.getAllSleepForUser(userId).size
        if (sleepCount >= 1) checkAndUnlockAchievement(userId, "SLEEP", 1)
        if (sleepCount >= 3) checkAndUnlockAchievement(userId, "SLEEP", 3)
        if (sleepCount >= 7) checkAndUnlockAchievement(userId, "SLEEP", 7)
        if (sleepCount >= 30) checkAndUnlockAchievement(userId, "SLEEP", 30)
        if (sleepCount >= 60) checkAndUnlockAchievement(userId, "SLEEP", 60)

        // META-CHAMPION
        val unlockedCount = achievementUserDao.countUnlockedByUser(userId)
        if (unlockedCount >= 5) checkAndUnlockAchievement(userId, "META_CHAMPION", 5)
        if (unlockedCount >= 15) checkAndUnlockAchievement(userId, "META_CHAMPION", 15)
        if (unlockedCount >= 30) checkAndUnlockAchievement(userId, "META_CHAMPION", 30)
    }
}