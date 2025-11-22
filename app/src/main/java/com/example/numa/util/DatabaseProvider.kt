package com.example.numa.util

import android.content.Context
import androidx.room.Room
import com.example.numa.DataBase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: DataBase? = null

    fun getDatabase(context: Context): DataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DataBase::class.java,
                "NumaDB"
            ).fallbackToDestructiveMigration().build()

            INSTANCE = instance
            instance
        }
    }
}