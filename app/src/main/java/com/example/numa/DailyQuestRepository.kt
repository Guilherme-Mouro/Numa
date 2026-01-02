package com.example.numa

import com.example.numa.dao.DailyQuestDao
import com.example.numa.entity.DailyQuest
import java.util.Calendar

class DailyQuestRepository(private val dailyQuestDao: DailyQuestDao) {

    // Lista de tipos para usares como referência no código
    companion object {
        const val TYPE_HABIT = "HABIT"       // Completar hábito
        const val TYPE_SLEEP = "SLEEP"       // Registar sono
        const val TYPE_CREATE = "CREATE"     // Criar hábito
        const val TYPE_SHOP = "SHOP"         // Comprar na loja
    }

    // 1. Verifica e Gera Missões
    suspend fun checkAndGenerateQuests(userId: Int) {
        val existingQuests = dailyQuestDao.getQuestsByUser(userId)
        val todayStart = getStartOfDay()

        // Se não houver missões ou se as missões forem de um dia anterior -> RESET
        if (existingQuests.isEmpty() || existingQuests[0].date != todayStart) {
            dailyQuestDao.clearQuestsForUser(userId)

            // Cria as missões
            val newQuests = listOf(
                DailyQuest(userId = userId, type = TYPE_HABIT, description = "Completar um hábito", target = 1, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_SLEEP, description = "Registar o sono", target = 1, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_CREATE, description = "Criar um hábito", target = 1, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_HABIT, description = "Completar 2 hábitos", target = 2, date = todayStart),
                DailyQuest(userId = userId, type = TYPE_SHOP, description = "Comprar algo na loja", target = 1, date = todayStart)
            )
            dailyQuestDao.insertQuests(newQuests)
        }
    }

    // 2. Atualizar Progresso (Chamado quando o user faz algo)
    suspend fun incrementProgress(userId: Int, type: String) {
        // Busca todas as missões ativas desse tipo (ex: completar 1 hábito e completar 2 hábitos)
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

    // Utilitário para pegar 00:00:00 do dia atual
    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}