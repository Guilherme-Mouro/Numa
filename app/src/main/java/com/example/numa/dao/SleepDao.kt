package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Sleep

@Dao
interface SleepDao {

    @Query("SELECT * FROM sleep WHERE userId = :userId")
    suspend fun getSleepByUser(userId: Int): List<Sleep>

    @Query("SELECT * FROM sleep WHERE id = :sleepId")
    suspend fun getSleepById(sleepId: Int): Sleep?

    @Insert
    suspend fun insertSleep(sleep: Sleep): Long

    @Delete
    suspend fun deleteSleep(sleep: Sleep)

    @Query("UPDATE sleep SET duration = :duration, timesAwake = :timesAwake, score = :score, points = :points, experience = :experience WHERE id = :sleepId")
    suspend fun updateSleep(
        sleepId: Int,
        duration: Long,
        timesAwake: Int,
        score: Double,
        points: Int,
        experience: Int
    )
}
