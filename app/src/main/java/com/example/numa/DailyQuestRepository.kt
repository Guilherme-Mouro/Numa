package com.example.numa

import com.example.numa.dao.DailyQuestDao
import com.example.numa.entity.DailyQuest
import java.util.Calendar

class DailyQuestRepository(private val dailyQuestDao: DailyQuestDao) {

    companion object {
        const val TYPE_HABIT = "HABIT"
        const val TYPE_SLEEP = "SLEEP"
        const val TYPE_CREATE = "CREATE"
        const val TYPE_SHOP = "SHOP"
    }

    suspend fun checkAndGenerateQuests(userId: Int) {
        val existingQuests = dailyQuestDao.getQuestsByUser(userId)
        val todayStart = getStartOfDay()

        if (existingQuests.isEmpty() || existingQuests[0].date != todayStart) {
            dailyQuestDao.clearQuestsForUser(userId)

            val newQuests = listOf(
                DailyQuest(userId = userId, type = TYPE_HABIT, description = "Complete a habit", target = 1, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_SLEEP, description = "Track your sleep", target = 1, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_CREATE, description = "Create a habit", target = 1, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_HABIT, description = "Complete two habits", target = 2, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_SHOP, description = "Buy something in the shop", target = 1, date = todayStart)
            )
            dailyQuestDao.insertQuests(newQuests)
        }
    }

    suspend fun incrementProgress(userId: Int, type: String) {
        val quests = dailyQuestDao.getActiveQuestsByType(userId, type)

        for (quest in quests) {
            val newProgress = quest.progress + 1
            val isComplete = newProgress >= quest.target

            dailyQuestDao.updateProgress(quest.id, newProgress, isComplete)
        }
    }

    suspend fun getQuests(userId: Int): List<DailyQuest> {
        return dailyQuestDao.getQuestsByUser(userId)
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}