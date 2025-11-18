package com.example.numa

import android.content.Context
import android.os.Bundle
import android.view.View
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.core.content.edit
import kotlinx.coroutines.launch
import com.example.numa.databinding.ActivityMainBinding
import com.example.numa.fragment.HabitFragment
import com.example.numa.fragment.HomeFragment
import com.example.numa.fragment.QuestFragment
import com.example.numa.fragment.SleepFragment
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastSelectedView: View? = null
    // 🛑 REMOVIDO: private lateinit var db: DataBase

    // Lazy initialization
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val achievementRepository by lazy {
        AchievementRepository(database.achievementDao())
    }
    private val sharedPref by lazy {
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ INICIALIZAR ACHIEVEMENTS
        initializeAchievements()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeMenuItem(HomeFragment())

        lastSelectedView = binding.bottomNav.findViewById(R.id.home)
        resizeIcon(lastSelectedView, null)
        changePage()

        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()


        lifecycleScope.launch {
            userId?.let {
                val user = database.userDao().getUserById(userId) 

                user?.let {
                    binding.tvUserName.text = it.name
                }
            }
        }
    }

    private fun changeMenuItem(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    private fun changePage() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val selectedView = binding.bottomNav.findViewById<View>(item.itemId)

            when (item.itemId) {
                R.id.home -> changeMenuItem(HomeFragment())
                R.id.habit -> changeMenuItem(HabitFragment())
                R.id.sleep -> changeMenuItem(SleepFragment())
                R.id.quest -> changeMenuItem(QuestFragment())
            }

            resizeIcon(selectedView, lastSelectedView)
            lastSelectedView = selectedView

            true
        }
    }

    private fun initializeAchievements() {
        lifecycleScope.launch {
            try {
                val isInitialized = sharedPref.getBoolean("achievements_initialized", false)

                if (!isInitialized) {
                    Log.d("MainActivity", "Inicializando achievements...")
                    achievementRepository.initializeAchievements()
                    sharedPref.edit {
                        putBoolean("achievements_initialized", true)
                    }
                    Log.d("MainActivity", "✅ Achievements inicializados com sucesso!")
                } else {
                    Log.d("MainActivity", "Achievements já inicializados")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ Erro ao inicializar achievements: ${e.message}")
            }
        }
    }

    private fun resizeIcon(selectedView: View?, previousView: View?) {
        previousView?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(150)?.start()
        selectedView?.animate()?.scaleX(1.3f)?.scaleY(1.3f)?.setDuration(200)?.start()
    }
}