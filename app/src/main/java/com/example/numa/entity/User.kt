package com.example.numa.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val username: String,
    val sequence: Int,
    val points: Int,

    )
