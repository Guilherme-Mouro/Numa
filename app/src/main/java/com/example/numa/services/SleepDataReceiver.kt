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

            // Define o mesmo período de "hoje" que o fragmento usa (desde as 18h de ontem)
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

                    // FILTRO DE RELEVÂNCIA: Apenas guardar eventos que começaram no período de "hoje".
                    //if (event.startTimeMillis < sinceMillis) {
                       // Log.w("SleepDataReceiver", "  -> IGNORADO (Evento é de um dia anterior)")
                        //continue
                    //}

                    if (event.status == SleepSegmentEvent.STATUS_SUCCESSFUL) {
                        val newSleep = Sleep(
                            userId = userId,
                            date = event.startTimeMillis,
                            startTime = event.startTimeMillis,
                            endTime = event.endTimeMillis,
                            timesAwake = 0,
                            snoring = false,
                            snoringAudioPath = null,
                            noiseLevel = 0.0,
                            score = 85.0, // Valor de exemplo
                            quality = "Bom", // Valor de exemplo
                            points = 100,
                            experience = 50
                        )
                        sleepDao.insertSleep(newSleep)
                        Log.i("SleepDataReceiver", "  -> CRIADO um novo registo de sono.")
                    }
                }
            }
        }
    }
}
