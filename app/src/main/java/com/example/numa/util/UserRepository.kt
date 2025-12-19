package com.example.numa.util

import com.example.numa.dao.UserDao

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
}