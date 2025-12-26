package com.example.numa.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.numa.AchievementRepository
import com.example.numa.R
import com.example.numa.databinding.ActivityMainBinding
import com.example.numa.fragment.HabitFragment
import com.example.numa.fragment.HomeFragment
import com.example.numa.fragment.QuestFragment
import com.example.numa.fragment.ShopFragment
import com.example.numa.fragment.SleepFragment
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val menuOrder = listOf(
        R.id.home,
        R.id.shop,
        R.id.habit,
        R.id.sleep,
        R.id.quest
    )
    private var lastMenuItemId: Int = R.id.home


    private lateinit var binding: ActivityMainBinding
    private var lastSelectedView: View? = null
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val achievementRepository by lazy {
        AchievementRepository(database.achievementDao())
    }
    private val sharedPref by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeAchievements()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeMenuItem(HomeFragment(), R.id.home)

        lastSelectedView = binding.bottomNav.findViewById(R.id.home)
        resizeIcon(lastSelectedView, null)
        changePage()

        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        //sessionManager.deleteUserId()

        loadUserDetails(userId)
    }

    private fun loadUserDetails(userId: Int?) {

        lifecycleScope.launch {
            userId?.let {
                val user = database.userDao().getUserById(userId)
                user?.let {
                    binding.tvUserName.text = it.name
                    binding.tvUserLevel.text = it.level.toString()
                    binding.tvPoints.text = it.points.toString()
                }
            }
        }
    }

    private fun changeMenuItem(fragment: Fragment, newItemId: Int) {

        val lastIndex = menuOrder.indexOf(lastMenuItemId)
        val newIndex = menuOrder.indexOf(newItemId)

        val transaction = supportFragmentManager.beginTransaction()

        if (newIndex > lastIndex) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else if (newIndex < lastIndex) {
            transaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction.replace(R.id.frameLayout, fragment)
            .commit()

        lastMenuItemId = newItemId
    }

    private fun changePage() {
        binding.bottomNav.setOnItemSelectedListener { item ->

            val selectedView = binding.bottomNav.findViewById<View>(item.itemId)

            when (item.itemId) {
                R.id.home -> changeMenuItem(HomeFragment(), item.itemId)
                R.id.shop -> changeMenuItem(ShopFragment(), item.itemId)
                R.id.habit -> changeMenuItem(HabitFragment(), item.itemId)
                R.id.sleep -> changeMenuItem(SleepFragment(), item.itemId)
                R.id.quest -> changeMenuItem(QuestFragment(), item.itemId)
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
    private fun clearSharedPreferencesForTesting() {
        Log.w("MainActivity", "🚨 LIMPEZA DE DADOS: Apagando 'app_prefs' para teste.")

        // Limpa todas as chaves no arquivo "app_prefs"
        sharedPref.edit {
            clear()
        }
    }

    private fun resizeIcon(selectedView: View?, previousView: View?) {
        previousView?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(150)?.start()
        selectedView?.animate()?.scaleX(1.3f)?.scaleY(1.3f)?.setDuration(200)?.start()
    }
}