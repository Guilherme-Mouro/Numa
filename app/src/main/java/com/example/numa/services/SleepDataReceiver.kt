package com.example.numa.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
import kotlin.math.min

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

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 18)
            val sinceMillis = calendar.timeInMillis

            CoroutineScope(Dispatchers.IO).launch {
                val sleepDao = DatabaseProvider.getDatabase(context).sleepDao()

                for (event in sleepEvents) {
                    val startTimeFormatted = timeFormat.format(Date(event.startTimeMillis))
                    val endTimeFormatted = timeFormat.format(Date(event.endTimeMillis))
                    Log.d("SleepDataReceiver", "> Processando evento: Início=$startTimeFormatted, Fim=$endTimeFormatted, Status=${event.status}")

                    if (event.startTimeMillis < sinceMillis) {
                        Log.w("SleepDataReceiver", "  -> IGNORADO (Evento é de um dia anterior)")
                        continue
                    }

                    if (event.status == SleepSegmentEvent.STATUS_SUCCESSFUL) {
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
                    }
                }
            }
        }
    }

    private fun calculateScore(durationMinutes: Long): Double {
        val targetMinutes = TimeUnit.HOURS.toMinutes(8) // Objetivo de 8 horas
        // Calcula o score como uma percentagem do objetivo, limitado a 100%
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
        // Mapeia a duração para um valor de XP entre 10 e 30
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
