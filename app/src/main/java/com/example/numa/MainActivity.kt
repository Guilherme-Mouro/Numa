package com.example.numa

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
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
    private lateinit var db: DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeMenuItem(HomeFragment())

        lastSelectedView = binding.bottomNav.findViewById(R.id.home)
        resizeIcon(lastSelectedView, null)
        changePage()

        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        db = Room.databaseBuilder(
            this,
            DataBase::class.java,
            "NumaDB"
        ).fallbackToDestructiveMigration().build()

        lifecycleScope.launch {
            userId?.let {
                val user = db.userDao().getUserById(userId)

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

    private fun resizeIcon(selectedView: View?, previousView: View?) {
        previousView?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(150)?.start()

        selectedView?.animate()?.scaleX(1.3f)?.scaleY(1.3f)?.setDuration(200)?.start()
    }
}
