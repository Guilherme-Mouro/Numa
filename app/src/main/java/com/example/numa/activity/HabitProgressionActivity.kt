package com.example.numa.activity

import android.content.Intent // Importante importar isto
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.numa.databinding.ActivityHabitProgressionBinding
import com.example.numa.entity.Habit
import com.example.numa.util.AlarmReceiver
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.LongFormatter
import kotlinx.coroutines.launch

class HabitProgressionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHabitProgressionBinding
    private lateinit var timer: CountDownTimer
    private val db by lazy { DatabaseProvider.getDatabase(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitProgressionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val habitId = intent.getIntExtra("habitId", -1)

        binding.btnFinishHabit.setOnClickListener {
            lifecycleScope.launch {
                val habit = db.habitDao().getHabitById(habitId)!!

                habit.streak++
                habit.state = "complete"

                db.habitDao().updateHabit(habit)

                val intent = Intent(this@HabitProgressionActivity, HabitActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        lifecycleScope.launch {
            val habit = db.habitDao().getHabitById(habitId)

            habit?.let {
                timer = object : CountDownTimer(habit.duration, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.tvTimer.text = LongFormatter.toTime(millisUntilFinished)

                        val progress = (millisUntilFinished * 100) / habit.duration
                        binding.progressBar.progress = progress.toInt()
                    }

                    override fun onFinish() {
                        binding.tvTimer.text = "00:00"
                        binding.progressBar.progress = 0

                        val intent =
                            Intent(this@HabitProgressionActivity, AlarmReceiver::class.java)
                        sendBroadcast(intent)
                    }
                }.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}