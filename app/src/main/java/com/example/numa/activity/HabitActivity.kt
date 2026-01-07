package com.example.numa.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.numa.databinding.ActivityHabitBinding
import com.example.numa.util.DatabaseProvider
import kotlinx.coroutines.launch
import android.view.View
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

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
                    binding.tvPoints.text = habit.streak.toString() + if (habit.streak == 1) " Day" else " Days"

                    // 1. Obter dados de Hoje
                    val today = LocalDate.now()
                    val currentDayOfWeek = today.dayOfWeek.name

                    // Timestamp do início de hoje (00:00:00) para comparação
                    val todayStart = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

                    // 2. Verificar se está AGENDADO para hoje
                    var isScheduledForToday = false

                    if (habit.isRecurring) {
                        if (habit.dayOfWeek == "EVERYDAY" || habit.dayOfWeek == currentDayOfWeek) {
                            isScheduledForToday = true
                        }
                    } else {
                        habit.specificDate?.let { millis ->
                            val habitDate = org.threeten.bp.Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            if (habitDate.isEqual(today)) {
                                isScheduledForToday = true
                            }
                        }
                    }

                    // 3. Verificar se JÁ FOI COMPLETADO hoje
                    // Se a data da última conclusão for maior que o início de hoje, já foi feito.
                    val isCompletedToday = habit.lastCompletedDate >= todayStart

                    // 4. Lógica Final:
                    // Mostra o botão APENAS SE: (É para hoje) E (Ainda NÃO foi completado)
                    if (isScheduledForToday && !isCompletedToday) {
                        binding.btnStartHabit.visibility = View.VISIBLE
                    } else {
                        binding.btnStartHabit.visibility = View.GONE

                    }
                }
            }
        }
    }
}