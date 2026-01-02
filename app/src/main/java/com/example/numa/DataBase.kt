package com.example.numa

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.numa.dao.AchievementDao
import com.example.numa.dao.AchievementUserDao
import com.example.numa.dao.HabitDao
import com.example.numa.dao.PetDao
import com.example.numa.dao.ShopItemDao
import com.example.numa.dao.SleepDao
import com.example.numa.dao.UserDao
import com.example.numa.dao.UserItemDao
import com.example.numa.entity.Achievement
import com.example.numa.entity.Habit
import com.example.numa.entity.Pet
import com.example.numa.entity.Sleep
import com.example.numa.entity.User
import com.example.numa.entity.AchievementUser
import com.example.numa.entity.ShopItem
import com.example.numa.entity.UserItem
import com.example.numa.dao.DailyQuestDao
import com.example.numa.entity.DailyQuest


@Database(
    entities = [
        User::class,
        Pet::class,
        Habit::class,
        Sleep::class,
        Achievement::class,
        AchievementUser::class,
        ShopItem::class,
        UserItem::class,
        DailyQuest::class,
               ],
    version = 12,
    exportSchema = false
)

abstract class DataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun petDao(): PetDao
    abstract fun habitDao(): HabitDao
    abstract fun sleepDao(): SleepDao
    abstract fun achievementDao(): AchievementDao
    abstract fun achievementUserDao(): AchievementUserDao
    abstract fun shopItemDao(): ShopItemDao
    abstract fun userItemDao(): UserItemDao

    abstract fun dailyQuestDao(): DailyQuestDao

}