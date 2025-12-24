package com.example.numa.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.numa.util.UserRepository
import com.example.numa.entity.Sleep
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import com.google.android.gms.location.SleepSegmentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class SleepDataReceiver : BroadcastReceiver() {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent != null && SleepSegmentEvent.hasEvents(intent)) {
            val sleepEvents = SleepSegmentEvent.extractEvents(intent)
            Log.i("SleepDataReceiver", "Recebidos ${sleepEvents.size} eventos de sono da API.")

            val sessionManager = SessionManager(context)
            val userId = sessionManager.getUserId()

            if (userId == null) {
                Log.e("SleepDataReceiver", "Utilizador não logado. A descartar eventos.")
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                val database = DatabaseProvider.getDatabase(context)
                val sleepDao = database.sleepDao()
                val userDao = database.userDao()
                val userRepository = UserRepository(userDao)

                for (event in sleepEvents) {
                    val startTimeFormatted = timeFormat.format(Date(event.startTimeMillis))
                    val endTimeFormatted = timeFormat.format(Date(event.endTimeMillis))
                    Log.d("SleepDataReceiver", "> Processando evento: Início=$startTimeFormatted, Fim=$endTimeFormatted, Status=${event.status}")

                    // FILTRO DE RELEVÂNCIA: Apenas guardar eventos que começaram no período de "hoje".
                    //if (event.startTimeMillis < sinceMillis) {
                    // Log.w("SleepDataReceiver", "  -> IGNORADO (Evento é de um dia anterior)")
                    //continue
                    //}

                    if (event.status == SleepSegmentEvent.STATUS_SUCCESSFUL) {
                        // VERIFICAÇÃO DE DUPLICADOS: Já existe um registo com este startTime e endTime?
                        val segmentExists = sleepDao.doesSegmentExist(userId, event.startTimeMillis, event.endTimeMillis) > 0

                        if (segmentExists) {
                            Log.w("SleepDataReceiver", "  -> IGNORADO (Segmento duplicado já existe na base de dados)")
                            continue
                        }

                        val durationMillis = event.endTimeMillis - event.startTimeMillis
                        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)

                        val score = calculateScore(durationMinutes)
                        val quality = getQualityFromScore(score)
                        val experience = calculateExperience(durationMinutes)

                        val newSleep = Sleep(
                            userId = userId,
                            date = event.startTimeMillis,
                            startTime = event.startTimeMillis,
                            endTime = event.endTimeMillis,
                            timesAwake = 0,
                            snoring = false,
                            snoringAudioPath = null,
                            noiseLevel = 0.0,
                            score = score,
                            quality = quality,
                            points = score.toInt(),
                            experience = experience
                        )
                        sleepDao.insertSleep(newSleep)
                        Log.i("SleepDataReceiver", "  -> CRIADO novo registo de sono com Score=$score, Quality='$quality', XP=$experience")
                        userRepository.addXpAndPoints(
                            userId = userId,
                            xpEarned = experience,
                            pointsEarned = score.toInt()
                        )
                    }
                }
            }
        }
    }

    private fun calculateScore(durationMinutes: Long): Double {
        val targetMinutes = TimeUnit.HOURS.toMinutes(8) // Objetivo de 8 horas
        val score = (durationMinutes.toDouble() / targetMinutes * 100).coerceAtMost(100.0)
        return score
    }

    private fun getQualityFromScore(score: Double): String {
        return when {
            score >= 90 -> "Excelente"
            score >= 80 -> "Bom"
            score >= 60 -> "Razoável"
            else -> "Fraco"
        }
    }

    private fun calculateExperience(durationMinutes: Long): Int {
        val xp = when {
            durationMinutes >= TimeUnit.HOURS.toMinutes(8) -> 30
            durationMinutes >= TimeUnit.HOURS.toMinutes(7) -> 25
            durationMinutes >= TimeUnit.HOURS.toMinutes(6) -> 20
            durationMinutes >= TimeUnit.HOURS.toMinutes(4) -> 15
            else -> 10
        }
        return xp
    }
}
