package com.example.numa.util

import java.text.DecimalFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object LongFormatter {

    fun toTime(value: Long): String {
        val seconds = ((value / 1000.0) % 60.0)
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(value) % 60).toInt()
        val formatSeconds = DecimalFormat("00").format(seconds)
        return String.format(Locale.US, "%02d:$formatSeconds", minutes)
    }
}