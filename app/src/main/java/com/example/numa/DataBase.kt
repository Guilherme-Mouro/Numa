package com.example.numa

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.numa.entity.Habit
import com.example.numa.entity.Pet
import com.example.numa.entity.User

@Database(entities = [User::class, Pet::class, Habit::class], version = 1, exportSchema = false)
abstract class DataBase : RoomDatabase() {

}