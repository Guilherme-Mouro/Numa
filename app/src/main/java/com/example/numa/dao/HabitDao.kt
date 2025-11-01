package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Habit

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit WHERE userId = :userId")
    suspend fun getHabitsByUser(userId: Int): List<Habit>

    @Query("SELECT * FROM habit WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): Habit?

    @Query("UPDATE habit SET title = :title, description = :description, startTime = :startTime, duration = :duration, experience = :experience, sequence = :sequence, state = :state WHERE id = :habitId")
    suspend fun updateHabit(
        habitId: Int,
        title: String,
        description: String,
        startTime: Long,
        duration: Long,
        experience: Int,
        sequence: Int,
        state: String
    )

    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Delete
    suspend fun deleteHabit(habit: Habit)
}
