package com.example.numa.util

import android.content.Context
import androidx.room.CoroutinesRoom
import androidx.room.Room
import com.example.numa.DataBase
import com.example.numa.data.DefaultShopItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

// Em DatabaseProvider.kt
    suspend fun addStoreItems(context: Context) {
        val db = getDatabase(context)

        // Apenas chame o insert. Como é suspend, ele vai esperar terminar.
        // Certifique-se que o método insertAll no DAO também seja 'suspend'
        db.shopItemDao().insertAll(DefaultShopItems.items)
    }
}