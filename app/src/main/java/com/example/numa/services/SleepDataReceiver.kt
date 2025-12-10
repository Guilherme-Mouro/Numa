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

class SleepDataReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("SleepDataReceiver", "Evento de sono recebido!")

        if (intent != null && SleepSegmentEvent.hasEvents(intent)) {
            val sleepEvents = SleepSegmentEvent.extractEvents(intent)
            val sessionManager = SessionManager(context)
            val userId = sessionManager.getUserId()

            if (userId == null) {
                Log.e("SleepDataReceiver", "Não foi possível salvar o evento de sono. O utilizador não está logado.")
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                val sleepDao = DatabaseProvider.getDatabase(context).sleepDao()

                for (event in sleepEvents) {
                    // Apenas guardar eventos de sono bem-sucedidos
                    if (event.status == SleepSegmentEvent.STATUS_SUCCESSFUL) {

                        // TODO: Estes valores são temporários. A pontuação, os pontos, etc., devem ser calculados com base na qualidade do sono.
                        val newSleep = Sleep(
                            userId = userId,
                            date = System.currentTimeMillis(),
                            startTime = event.startTimeMillis,
                            endTime = event.endTimeMillis,
                            timesAwake = 0, // A API não fornece isto diretamente, precisaria de outra lógica
                            snoring = false, // Precisaria de gravação de áudio
                            snoringAudioPath = null,
                            noiseLevel = 0.0, // Precisaria de gravação de áudio
                            score = 85.0, // Valor de exemplo
                            quality = "Bom", // Valor de exemplo
                            points = 100, // Valor de exemplo
                            experience = 50 // Valor de exemplo
                        )

                        sleepDao.insertSleep(newSleep)
                        Log.d("SleepDataReceiver", "Novo registo de sono inserido na base de dados para o utilizador $userId")
                    }
                }
            }
        }
    }
}
