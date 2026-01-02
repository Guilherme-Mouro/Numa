package com.example.numa

import com.example.numa.dao.AchievementDao
import com.example.numa.entity.Achievement

class AchievementRepository(private val achievementDao: AchievementDao) {

    suspend fun initializeAchievements() {
        val achievements = listOf(
            // DAILY STREAK
            Achievement(
                type = "DAILY_STREAK",
                level = 1,
                title = "Primeiro Dia",
                description = "Completar pelo menos 1 hábito",
                points = 10,
                experience = 5,

            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 7,
                title = "Semana Ativa",
                description = "Manter 7 dias com ≥1 hábito",
                points = 80,
                experience = 40,
            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 30,
                title = "Mês Imparável",
                description = "Manter 30 dias com ≥1 hábito",
                points = 300,
                experience = 150,
            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 90,
                title = "Trimestre Consistente",
                description = "Manter 90 dias com ≥1 hábito",
                points = 600,
                experience = 300,
            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 365,
                title = "Lenda Anual",
                description = "Manter 365 dias com ≥1 hábito",
                points = 2000,
                experience = 1000,
            ),

            // HABIT STREAK
            Achievement(
                type = "HABIT_STREAK",
                level = 2,
                title = "Inicio de jornada",
                description = "Completar o mesmo hábito 2 vezes seguidas",
                points = 60,
                experience = 30,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 7,
                title = "Comprometido",
                description = "Completar o mesmo hábito 7 vezes seguidas",
                points = 60,
                experience = 30,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 30,
                title = "Disciplinado",
                description = "Completar o mesmo hábito 30 vezes seguidas",
                points = 150,
                experience = 75,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 60,
                title = "Mestre da Rotina",
                description = "Completar o mesmo hábito 60 vezes seguidas",
                points = 300,
                experience = 150,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 100,
                title = "Obsessão Produtiva",
                description = "Completar o mesmo hábito 100 vezes seguidas",
                points = 500,
                experience = 250,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 365,
                title = "Imortal",
                description = "Completar o mesmo hábito 365 vezes seguidas",
                points = 1000,
                experience = 500,
            ),

            // COLECIONADOR
            Achievement(
                type = "COLECIONADOR",
                level = 1,
                title = "Primeira Missão",
                description = "Criar primeiro hábito",
                points = 5,
                experience = 5,
            ),
            Achievement(
                type = "COLECIONADOR",
                level = 3,
                title = "Triplo Combo",
                description = "Criar 3 hábitos",
                points = 40,
                experience = 20,
            ),
            Achievement(
                type = "COLECIONADOR",
                level = 10,
                title = "Decalógico",
                description = "Criar 10 hábitos",
                points = 120,
                experience = 60,
            ),
            Achievement(
                type = "COLECIONADOR",
                level = 20,
                title = "Vinte Metas",
                description = "Criar 20 hábitos",
                points = 250,
                experience = 125,
            ),
            Achievement(
                type = "COLECIONADOR",
                level = 50,
                title = "Colecionador Extremo",
                description = "Criar 50 hábitos",
                points = 500,
                experience = 250,
            ),

            // META-CHAMPION
            Achievement(
                type = "META_CHAMPION",
                level = 5,
                title = "Champion Iniciante",
                description = "Completar 5 achievements",
                points = 150,
                experience = 75,
            ),
            Achievement(
                type = "META_CHAMPION",
                level = 15,
                title = "Champion Veterano",
                description = "Completar 15 achievements",
                points = 350,
                experience = 175,
            ),
            Achievement(
                type = "META_CHAMPION",
                level = 30,
                title = "Champion Lendário",
                description = "Completar 30 achievements",
                points = 700,
                experience = 350,
            ),
            // SLEEP
            Achievement(
                type = "SLEEP",
                level = 1,
                title = "Primeira Noite",
                description = "Registar 1 sono",
                points = 10,
                experience = 5,
            ),
            Achievement(
                type = "SLEEP",
                level = 3,
                title = "Três Noites",
                description = "Registar 3 sonos",
                points = 30,
                experience = 15,
            ),
            Achievement(
                type = "SLEEP",
                level = 7,
                title = "Semana de Descanso",
                description = "Registar 7 sonos",
                points = 70,
                experience = 35,
            ),
            Achievement(
                type = "SLEEP",
                level = 30,
                title = "Mês de Sono",
                description = "Registar 30 sonos",
                points = 250,
                experience = 125,
            ),
            Achievement(
                type = "SLEEP",
                level = 60,
                title = "Dorminhoco Dedicado",
                description = "Registar 60 sonos",
                points = 500,
                experience = 250,
            )
        )

        achievements.forEach { achievement ->
            if (achievementDao.getAchievementByTypeAndLevel(achievement.type, achievement.level) == null) {
                achievementDao.insertAchievement(achievement)
            }
        }
    }
}