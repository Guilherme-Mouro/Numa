package com.example.numa.Utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {

    // Converts a plain password into a hashed (encrypted) version
    fun hashPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt())
    }

    // Verifies that a plain password matches the hashed password
    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }

}
