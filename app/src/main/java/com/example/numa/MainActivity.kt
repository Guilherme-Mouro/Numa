package com.example.numa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.numa.databinding.ActivityMainBinding
import com.example.numa.fragment.HabitFragment
import com.example.numa.fragment.HomeFragment
import com.example.numa.fragment.QuestFragment
import com.example.numa.fragment.SleepFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeMenuItem(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> changeMenuItem(HomeFragment())
                R.id.habit -> changeMenuItem(HabitFragment())
                R.id.sleep -> changeMenuItem(SleepFragment())
                R.id.quest -> changeMenuItem(QuestFragment())
            }
            true
        }
    }

    private fun changeMenuItem(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}