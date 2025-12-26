package com.example.numa

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class NumaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar a biblioteca de datas e horas (ThreeTenABP)
        AndroidThreeTen.init(this)
    }
}