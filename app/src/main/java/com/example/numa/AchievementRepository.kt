package com.example.numa

import com.example.numa.dao.AchievementDao
import com.example.numa.entity.Achievement

class AchievementRepository(private val achievementDao: AchievementDao) {

    suspend fun initializeAchievements() {
        val achievements = listOf(
            // DAILY STREAK (Theme: FIRE / HEAT)
            Achievement(
                type = "DAILY_STREAK",
                level = 1,
                title = "First Spark",
                description = "Complete at least 1 habit",
                points = 10,
                experience = 5,

                ),
            Achievement(
                type = "DAILY_STREAK",
                level = 7,
                title = "Burning Week",
                description = "Maintain 7 days with ≥1 habit",
                points = 80,
                experience = 40,
            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 30,
                title = "Monthly Inferno",
                description = "Maintain 30 days with ≥1 habit",
                points = 300,
                experience = 150,
            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 90,
                title = "Blazing Quarter",
                description = "Maintain 90 days with ≥1 habit",
                points = 600,
                experience = 300,
            ),
            Achievement(
                type = "DAILY_STREAK",
                level = 365,
                title = "Eternal Flame",
                description = "Maintain 365 days with ≥1 habit",
                points = 2000,
                experience = 1000,
            ),

            // HABIT STREAK (Theme: CHAIN / STRENGTH)
            Achievement(
                type = "HABIT_STREAK",
                level = 2,
                title = "First Link",
                description = "Complete the same habit 2 times in a row",
                points = 60,
                experience = 30,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 7,
                title = "Iron Chain",
                description = "Complete the same habit 7 times in a row",
                points = 60,
                experience = 30,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 30,
                title = "Unbreakable",
                description = "Complete the same habit 30 times in a row",
                points = 150,
                experience = 75,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 60,
                title = "Steel Will",
                description = "Complete the same habit 60 times in a row",
                points = 300,
                experience = 150,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 100,
                title = "Titanium Grip",
                description = "Complete the same habit 100 times in a row",
                points = 500,
                experience = 250,
            ),
            Achievement(
                type = "HABIT_STREAK",
                level = 365,
                title = "Perpetual Motion",
                description = "Complete the same habit 365 times in a row",
                points = 1000,
                experience = 500,
            ),

            // COLLECTOR (Theme: ARCHITECT / BUILDER)
            Achievement(
                type = "COLLECTOR",
                level = 1,
                title = "First Brick",
                description = "Create your first habit",
                points = 5,
                experience = 5,
            ),
            Achievement(
                type = "COLLECTOR",
                level = 3,
                title = "The Blueprint",
                description = "Create 3 habits",
                points = 40,
                experience = 20,
            ),
            Achievement(
                type = "COLLECTOR",
                level = 10,
                title = "The Architect",
                description = "Create 10 habits",
                points = 120,
                experience = 60,
            ),
            Achievement(
                type = "COLLECTOR",
                level = 20,
                title = "Life Designer",
                description = "Create 20 habits",
                points = 250,
                experience = 125,
            ),
            Achievement(
                type = "COLLECTOR",
                level = 50,
                title = "Master Builder",
                description = "Create 50 habits",
                points = 500,
                experience = 250,
            ),

            // META-CHAMPION (Theme: ROYALTY / RANKS)
            Achievement(
                type = "META_CHAMPION",
                level = 5,
                title = "Rising Squire",
                description = "Complete 5 achievements",
                points = 150,
                experience = 75,
            ),
            Achievement(
                type = "META_CHAMPION",
                level = 15,
                title = "Honor Knight",
                description = "Complete 15 achievements",
                points = 350,
                experience = 175,
            ),
            Achievement(
                type = "META_CHAMPION",
                level = 30,
                title = "Emperor of Habits",
                description = "Complete 30 achievements",
                points = 700,
                experience = 350,
            ),

            // SLEEP (Theme: COSMOS / DREAM)
            Achievement(
                type = "SLEEP",
                level = 1,
                title = "Moonwalker",
                description = "Log 1 sleep",
                points = 10,
                experience = 5,
            ),
            Achievement(
                type = "SLEEP",
                level = 3,
                title = "Stardust",
                description = "Log 3 sleeps",
                points = 30,
                experience = 15,
            ),
            Achievement(
                type = "SLEEP",
                level = 7,
                title = "Deep Diver",
                description = "Log 7 sleeps",
                points = 70,
                experience = 35,
            ),
            Achievement(
                type = "SLEEP",
                level = 30,
                title = "Dream Weaver",
                description = "Log 30 sleeps",
                points = 250,
                experience = 125,
            ),
            Achievement(
                type = "SLEEP",
                level = 60,
                title = "Astral Traveler",
                description = "Log 60 sleeps",
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