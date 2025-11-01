package com.example.numa.util

object LevelUp {
    fun xpForLevel(level: Int): Int {
        return (100 * level * 0.75).toInt()
    }
}