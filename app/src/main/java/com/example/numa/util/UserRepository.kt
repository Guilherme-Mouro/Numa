package com.example.numa.util

import com.example.numa.dao.UserDao
import java.util.Calendar


class UserRepository(private val userDao: UserDao) {

    /**
     * Função universal para adicionar recompensas.
     * Pode ser chamada ao completar hábitos, dormir, quests, etc.
     */
    suspend fun addXpAndPoints(userId: Int, xpEarned: Int, pointsEarned: Int) {
        val user = userDao.getUserById(userId) ?: return

        var currentLevel = user.level
        var currentXp = user.experience + xpEarned
        var currentPoints = user.points + pointsEarned

        // Lógica de Level Up
        while (true) {
            val xpRequired = LevelUp.xpForLevel(currentLevel)

            if (currentXp >= xpRequired) {
                currentXp -= xpRequired
                currentLevel++
                // Opcional: Aqui podes enviar um evento ou log a dizer "Level Up!"
            } else {
                break
            }
        }

        val updatedUser = user.copy(
            level = currentLevel,
            experience = currentXp,
            points = currentPoints
        )

        userDao.updateUser(updatedUser)
    }
    // ✅ NOVA FUNÇÃO: Atualiza a streak diária
    suspend fun updateDailyStreak(userId: Int) {
        val user = userDao.getUserById(userId) ?: return

        val today = getTodayTimestamp()
        val lastActiveDay = getDayTimestamp(user.lastActiveDate)

        // Se já atualizou hoje, não faz nada
        if (lastActiveDay == today) {
            return
        }

        val yesterday = getYesterdayTimestamp()

        val newStreak = when {
            // Primeiro dia ou reiniciando após quebra
            user.lastActiveDate == 0L || lastActiveDay < yesterday -> 1

            // Dia consecutivo
            lastActiveDay == yesterday -> user.streak + 1

            // Mesma streak (não deveria acontecer, mas por segurança)
            else -> user.streak
        }

        val updatedUser = user.copy(
            streak = newStreak,
            lastActiveDate = System.currentTimeMillis()
        )

        userDao.updateUser(updatedUser)
    }

    // Funções auxiliares para trabalhar com datas
    private fun getTodayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getYesterdayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getDayTimestamp(timestamp: Long): Long {
        if (timestamp == 0L) return 0L

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
