package com.example.numa

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class NumaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        AndroidThreeTen.init(this)
    }
}