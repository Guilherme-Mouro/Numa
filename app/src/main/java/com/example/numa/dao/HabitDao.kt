package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Habit
import java.time.DayOfWeek

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit WHERE userId = :userId")
    suspend fun getHabitsByUser(userId: Int): List<Habit>

    @Query("SELECT * FROM habit WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): Habit?

    @Query("""
    SELECT * FROM habit 
    WHERE userId = :userId AND state == "incomplete" AND (
        (isRecurring = 1 AND dayOfWeek = :dayOfWeek)
        OR
        (isRecurring = 0 AND specificDate = :specificDate)
    )
""")
    suspend fun getHabitsForDate(dayOfWeek: String, specificDate: Long, userId: Int): List<Habit>

    @Query("""
    SELECT * FROM habit 
    WHERE userId = :userId AND state == "complete" AND (
        (isRecurring = 1 AND dayOfWeek = :dayOfWeek)
        OR
        (isRecurring = 0 AND specificDate = :specificDate)
    )
""")
    suspend fun getCompletedHabitsForDate(dayOfWeek: String, specificDate: Long, userId: Int): List<Habit>


    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("UPDATE habit SET state = :state WHERE id = :habitId")
    suspend fun updateHabitState(
        habitId: Int,
        state: String
    )

    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Delete
    suspend fun deleteHabit(habit: Habit)
}
