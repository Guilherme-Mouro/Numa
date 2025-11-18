package com.example.numa.util

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt("USER_ID", userId).apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt("USER_ID", -1)
        return if (id != -1) id else null
    }

    fun deleteUserId() {
        prefs.edit().clear().apply()
    }
}