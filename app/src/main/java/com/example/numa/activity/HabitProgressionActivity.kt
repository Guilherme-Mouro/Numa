package com.example.numa.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.numa.databinding.ActivityHabitProgressionBinding
import com.example.numa.util.AlarmReceiver
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.LongFormatter
import com.example.numa.util.UserRepository
import kotlinx.coroutines.launch
import com.example.numa.CheckAchievementRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HabitProgressionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHabitProgressionBinding
    private lateinit var timer: CountDownTimer
    private lateinit var userRepository: UserRepository
    private lateinit var checkAchievementRepository: CheckAchievementRepository

    private val db by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitProgressionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(db.userDao())

        checkAchievementRepository = CheckAchievementRepository(
            db.achievementDao(),
            db.achievementUserDao(),
            db.userDao(),
            db.habitDao()
        )

        val habitId = intent.getIntExtra("habitId", -1)

        binding.btnFinishHabit.setOnClickListener {
            lifecycleScope.launch {
                val habit = db.habitDao().getHabitById(habitId)!!

                // ✅ Para hábitos recorrentes, NÃO muda o state, só o lastCompletedDate
                // Para hábitos de data específica, muda o state
                val updatedHabit = if (habit.isRecurring) {
                    habit.copy(
                        streak = habit.streak + 1,
                        lastCompletedDate = System.currentTimeMillis()
                        // state permanece "incomplete" para hábitos recorrentes
                    )
                } else {
                    habit.copy(
                        streak = habit.streak + 1,
                        state = "complete",
                        lastCompletedDate = System.currentTimeMillis()
                    )
                }

                // ✅ AGUARDA a atualização estar completa
                db.habitDao().updateHabit(updatedHabit)

                // ✅ Adiciona XP e Pontos
                userRepository.addXpAndPoints(
                    userId = habit.userId,
                    xpEarned = 10,
                    pointsEarned = 25
                )

                // ✅ Atualiza streak diário
                userRepository.updateDailyStreak(habit.userId)

                // ✅ AGORA verifica achievements - o hábito já foi atualizado no DB
                checkAchievementRepository.checkAllAchievements(habit.userId, habitId)

                val intent = Intent(this@HabitProgressionActivity, MainActivity::class.java)
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