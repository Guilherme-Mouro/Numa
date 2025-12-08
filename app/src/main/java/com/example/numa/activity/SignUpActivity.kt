package com.example.numa.activity

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.numa.R
import com.example.numa.databinding.ActivitySignUpBinding
import com.example.numa.entity.Pet
import com.example.numa.entity.User
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.FixPixelArt
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val db by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        val userID = sessionManager.getUserId()

        lifecycleScope.launch {
            db.shopItemDao().deleteAll()
            DatabaseProvider.addStoreItems(this@SignUpActivity)
        }

        if (userID != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val characterImageView = binding.imgAnim

        characterImageView.setBackgroundResource(R.drawable.cat_banner_animation)

        FixPixelArt.removeAnimFilter(characterImageView)

        val characterAnimation = characterImageView.background as AnimationDrawable
        characterAnimation.start()


        binding.btnSignUp.setOnClickListener {
            val newUser = User(
                name = binding.edName.text.toString(),
                streak = 0,
                points = 0,
                level = 1,
            )

            lifecycleScope.launch {
                val userId = db.userDao().insertUser(newUser).toInt()

                val sessionManager = SessionManager(this@SignUpActivity)
                sessionManager.saveUserId(userId)

                val newPet = Pet(
                    userId = userId,
                    name = binding.edPetName.text.toString(),
                    humor = "happy",
                    skin = "black_cat_idle_animation",
                    head = null,
                    torso = null,
                    legs = null,
                    feet = null,
                )

                db.petDao().insertPet(newPet)

                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}