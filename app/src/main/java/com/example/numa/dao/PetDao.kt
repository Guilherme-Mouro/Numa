package com.example.numa.dao

import androidx.room.*
import com.example.numa.entity.Pet

@Dao
interface PetDao {

    @Query("SELECT * FROM pet WHERE userId = :userId")
    suspend fun getPetByUser(userId: Int): Pet?

    @Query("UPDATE pet SET name = :name WHERE id = :petId")
    suspend fun updateName(petId: Int, name: String)

    @Query("UPDATE pet SET humor = :humor WHERE id = :petId")
    suspend fun updateHumor(petId: Int, humor: String)

    @Query("UPDATE pet SET skin = :skin WHERE id = :petId")
    suspend fun updateSkin(petId: Int, skin: String)

    @Query("UPDATE pet SET head = :head WHERE id = :petId")
    suspend fun updateHead(petId: Int, head: String)
    @Query("UPDATE pet SET torso = :torso WHERE id = :petId")
    suspend fun updateTorso(petId: Int, torso: String)
    @Query("UPDATE pet SET legs = :legs WHERE id = :petId")
    suspend fun updateLegs(petId: Int, legs: String)
    @Query("UPDATE pet SET feet = :feet WHERE id = :petId")
    suspend fun updateFeet(petId: Int, feet: String)

    @Insert
    suspend fun insertPet(pet: Pet): Long

    @Delete
    suspend fun deletePet(pet: Pet)
}
