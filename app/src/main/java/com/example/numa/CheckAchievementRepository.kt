package com.example.numa

import com.example.numa.dao.AchievementDao
import com.example.numa.dao.AchievementUserDao
import com.example.numa.dao.UserDao
import com.example.numa.dao.HabitDao
import com.example.numa.entity.AchievementUser

class CheckAchievementRepository(
    private val achievementDao: AchievementDao,
    private val achievementUserDao: AchievementUserDao,
    private val userDao: UserDao,
    private val habitDao: HabitDao
) {

    // ✅ 1. Inicializa o teu UserRepository aqui usando o userDao existente
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

            // ✅ 2. Usa a função do UserRepository para dar XP e Pontos
            userRepository.addXpAndPoints(
                userId = userId,
                xpEarned = achievement.experience,
                pointsEarned = achievement.points
            )
        }
    }

    // O resto da função mantém-se exatamente igual
    suspend fun checkAllAchievements(userId: Int, habitId: Int? = null) {
        val user = userDao.getUserById(userId) ?: return
        val habits = habitDao.getHabitsByUser(userId)

        // DAILY STREAK
        when (user.streak) {
            1 -> checkAndUnlockAchievement(userId, "DAILY_STREAK", 1)
            7 -> checkAndUnlockAchievement(userId, "DAILY_STREAK", 7)
            30 -> checkAndUnlockAchievement(userId, "DAILY_STREAK", 30)
            90 -> checkAndUnlockAchievement(userId, "DAILY_STREAK", 90)
            365 -> checkAndUnlockAchievement(userId, "DAILY_STREAK", 365)
        }

        // HABIT STREAK
        if (habitId != null) {
            val habit = habitDao.getHabitById(habitId)
            when (habit?.streak) {
                7 -> checkAndUnlockAchievement(userId, "HABIT_STREAK", 7)
                30 -> checkAndUnlockAchievement(userId, "HABIT_STREAK", 30)
                60 -> checkAndUnlockAchievement(userId, "HABIT_STREAK", 60)
                100 -> checkAndUnlockAchievement(userId, "HABIT_STREAK", 100)
                365 -> checkAndUnlockAchievement(userId, "HABIT_STREAK", 365)
            }
        }

        // COLECIONADOR
        when (habits.size) {
            1 -> checkAndUnlockAchievement(userId, "COLECIONADOR", 1)
            3 -> checkAndUnlockAchievement(userId, "COLECIONADOR", 3)
            10 -> checkAndUnlockAchievement(userId, "COLECIONADOR", 10)
            20 -> checkAndUnlockAchievement(userId, "COLECIONADOR", 20)
            50 -> checkAndUnlockAchievement(userId, "COLECIONADOR", 50)
        }

        // META-CHAMPION
        val unlockedCount = achievementUserDao.countUnlockedByUser(userId)
        when (unlockedCount) {
            5 -> checkAndUnlockAchievement(userId, "META_CHAMPION", 5)
            15 -> checkAndUnlockAchievement(userId, "META_CHAMPION", 15)
            30 -> checkAndUnlockAchievement(userId, "META_CHAMPION", 30)
        }
    }
}