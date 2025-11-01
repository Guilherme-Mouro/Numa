package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("UPDATE user SET name = :username WHERE id = :userId")
    suspend fun updateUsername(userId: Int, username: String)

    @Query("UPDATE user SET sequence = :sequence WHERE id = :userId")
    suspend fun updateSequence(userId: Int, sequence: Int)

    @Query("UPDATE user SET points = :points WHERE id = :userId")
    suspend fun updatePoints(userId: Int, points: Int)

    @Insert
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)
}
