package com.example.numa

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.numa.databinding.ActivitySignUpBinding
import com.example.numa.entity.User
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var biding: ActivitySignUpBinding

    private lateinit var db: DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(biding.root)

        val sessionManager = SessionManager(this)
        val userID = sessionManager.getUserId()

        if (userID != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        db = Room.databaseBuilder(
            this,
            DataBase::class.java,
            "NumaDB"
        ).fallbackToDestructiveMigration().build()

        biding.btnSignUp.setOnClickListener {
            val newUser = User(
                name = biding.edName.text.toString(),
                streak = 0,
                points = 0,
                level = 1,
            )

            lifecycleScope.launch {
                val userId = db.userDao().insertUser(newUser).toInt()

                val sessionManager = SessionManager(this@SignUpActivity)
                sessionManager.saveUserId(userId)

                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}