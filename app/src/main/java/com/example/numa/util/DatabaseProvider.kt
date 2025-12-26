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

    suspend fun addStoreItems(context: Context) {
        val db = getDatabase(context)
        val dao = db.shopItemDao()

        val existing = dao.getAllShopItem()
        val defaults = DefaultShopItems.items

        val newOnes = defaults.filter { defItem ->
            existing.none {
                it.name == defItem.name
                it.price == defItem.price
            }
        }

        if (newOnes.isNotEmpty()) {
            dao.deleteAll()
            dao.insertAll(newOnes)
        }
    }

}