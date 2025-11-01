package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Pet

@Dao
interface PetDao {

    @Query("SELECT * FROM pet WHERE userId = :userId")
    suspend fun getPetByUser(userId: Int): Pet?

    @Query("UPDATE pet SET name = :name WHERE id = :petId")
    suspend fun updateName(petId: Int, name: String)

    @Query("UPDATE pet SET level = :level WHERE id = :petId")
    suspend fun updateLevel(petId: Int, level: Int)

    @Query("UPDATE pet SET experience = :experience WHERE id = :petId")
    suspend fun updateExperience(petId: Int, experience: Int)

    @Query("UPDATE pet SET humor = :humor WHERE id = :petId")
    suspend fun updateHumor(petId: Int, humor: String)

    @Query("UPDATE pet SET sprite = :sprite WHERE id = :petId")
    suspend fun updateSprite(petId: Int, sprite: String)

    @Insert
    suspend fun insertPet(pet: Pet): Long

    @Delete
    suspend fun deletePet(pet: Pet)
}
