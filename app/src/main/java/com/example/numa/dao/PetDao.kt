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

    @Insert
    suspend fun insertPet(pet: Pet): Long

    @Delete
    suspend fun deletePet(pet: Pet)
}
