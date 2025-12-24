package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Sleep

@Dao
interface SleepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: Sleep): Long

    @Query("SELECT COUNT(*) FROM sleep WHERE userId = :userId AND startTime = :startTime AND endTime = :endTime")
    suspend fun doesSegmentExist(userId: Int, startTime: Long, endTime: Long): Int

    @Query("SELECT * FROM sleep WHERE userId = :userId ORDER BY startTime DESC LIMIT 7")
    suspend fun getLatest7SleepSegments(userId: Int): List<Sleep>

    @Query("SELECT * FROM sleep WHERE userId = :userId AND startTime >= :sinceMillis ORDER BY startTime DESC")
    suspend fun getSleepSegmentsSince(userId: Int, sinceMillis: Long): List<Sleep>

    @Query("SELECT * FROM sleep WHERE userId = :userId ORDER BY endTime DESC LIMIT 1")
    suspend fun getLatestSleepForUser(userId: Int): Sleep?

    @Query("SELECT * FROM sleep WHERE userId = :userId")
    suspend fun getAllSleepForUser(userId: Int): List<Sleep>

    @Query("SELECT * FROM sleep WHERE id = :sleepId")
    suspend fun getSleepById(sleepId: Int): Sleep?

    @Delete
    suspend fun deleteSleep(sleep: Sleep)

    @Query("UPDATE sleep SET timesAwake = :timesAwake, score = :score, points = :points, experience = :experience WHERE id = :sleepId")
    suspend fun updateSleep(
        sleepId: Int,
        timesAwake: Int,
        score: Double,
        points: Int,
        experience: Int
    )
}
