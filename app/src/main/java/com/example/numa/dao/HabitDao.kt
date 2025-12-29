package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Habit

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit WHERE userId = :userId")
    suspend fun getHabitsByUser(userId: Int): List<Habit>

    @Query("SELECT * FROM habit WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): Habit?

    // ✅ Query atualizada: Verifica se o hábito NÃO foi completado HOJE
    @Query("""
    SELECT * FROM habit 
    WHERE userId = :userId AND (
        (isRecurring = 1 AND (dayOfWeek = :dayOfWeek OR dayOfWeek = 'EVERYDAY') 
         AND (lastCompletedDate < :todayStart OR lastCompletedDate = 0))
        OR
        (isRecurring = 0 AND specificDate = :specificDate AND state = 'incomplete')
    )
""")
    suspend fun getHabitsForDate(
        dayOfWeek: String,
        specificDate: Long,
        userId: Int,
        todayStart: Long // Timestamp do início do dia (00:00:00)
    ): List<Habit>

    // ✅ Query para hábitos completados HOJE
    @Query("""
    SELECT * FROM habit 
    WHERE userId = :userId AND (
        (isRecurring = 1 AND (dayOfWeek = :dayOfWeek OR dayOfWeek = 'EVERYDAY')
         AND lastCompletedDate >= :todayStart)
        OR
        (isRecurring = 0 AND specificDate = :specificDate AND state = 'complete')
    )
""")
    suspend fun getCompletedHabitsForDate(
        dayOfWeek: String,
        specificDate: Long,
        userId: Int,
        todayStart: Long
    ): List<Habit>

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