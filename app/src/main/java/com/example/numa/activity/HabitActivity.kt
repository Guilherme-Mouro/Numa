package com.example.numa.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.numa.databinding.ActivityHabitBinding
import com.example.numa.util.DatabaseProvider
import kotlinx.coroutines.launch

class HabitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHabitBinding
    private val db by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val habitId = intent.getIntExtra("habitId", -1)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartHabit.setOnClickListener {
            lifecycleScope.launch {
                val intent = Intent(this@HabitActivity, HabitProgressionActivity::class.java)
                intent.putExtra("habitId",habitId)
                startActivity(intent)
            }
            finish()
        }

        loadHabit(habitId)
    }

    private fun loadHabit(habitId: Int?) {
        lifecycleScope.launch {
            habitId?.let {
                val habit = db.habitDao().getHabitById(habitId)

                habit?.let {
                    binding.tvHabitTitle.text = habit.title
                    binding.tvDesc.text = habit.description
                    binding.tvXp.text = habit.experience.toString() + " XP"
                    binding.tvPoints.text =
                        habit.streak.toString() + if (habit.streak == 1) "Day" else " Days"
                    //binding.numDuration.value = habit.duration.toInt()
                }
            }
        }
    }
}