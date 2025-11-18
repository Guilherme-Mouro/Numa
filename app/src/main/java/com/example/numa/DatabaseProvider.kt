package com.example.numa

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: DataBase? = null

    fun getDatabase(context: Context): DataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DataBase::class.java,
                "numa_database"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}