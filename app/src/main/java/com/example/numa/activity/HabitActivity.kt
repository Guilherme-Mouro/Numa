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
        // Receber a data selecionada (padrão = 0 se não vier)
        val selectedDateMillis = intent.getLongExtra("selectedDate", 0L)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartHabit.setOnClickListener {
            lifecycleScope.launch {
                val intent = Intent(this@HabitActivity, HabitProgressionActivity::class.java)
                intent.putExtra("habitId", habitId)
                startActivity(intent)
            }
            finish()
        }

        loadHabit(habitId, selectedDateMillis)
    }

    private fun loadHabit(habitId: Int?, selectedDateMillis: Long) {
        lifecycleScope.launch {
            habitId?.let {
                val habit = db.habitDao().getHabitById(habitId)

                habit?.let {
                    binding.tvHabitTitle.text = habit.title
                    binding.tvDesc.text = habit.description
                    binding.tvXp.text = habit.experience.toString() + " XP"
                    binding.tvPoints.text = habit.streak.toString() + if (habit.streak == 1) " Day" else " Days"

                    // 1. Usar a data SELECIONADA em vez de sempre "hoje"
                    val selectedDate = if (selectedDateMillis > 0) {
                        Instant.ofEpochMilli(selectedDateMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    } else {
                        LocalDate.now() // Fallback para hoje se não vier data
                    }

                    val today = LocalDate.now()
                    val selectedDayOfWeek = selectedDate.dayOfWeek.name

                    // 2. Verificar se está AGENDADO para a data SELECIONADA
                    var isScheduledForSelectedDate = false

                    if (habit.isRecurring) {
                        if (habit.dayOfWeek == "EVERYDAY" || habit.dayOfWeek == selectedDayOfWeek) {
                            isScheduledForSelectedDate = true
                        }
                    } else {
                        habit.specificDate?.let { millis ->
                            val habitDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            if (habitDate.isEqual(selectedDate)) {
                                isScheduledForSelectedDate = true
                            }
                        }
                    }

                    // 3. Verificar se JÁ FOI COMPLETADO HOJE (não na data selecionada, mas HOJE)
                    val isCompletedToday = if (habit.lastCompletedDate > 0) {
                        val completedDate = Instant.ofEpochMilli(habit.lastCompletedDate)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        completedDate.isEqual(today)
                    } else {
                        false
                    }

                    // 4. Lógica Final
                    // O botão só aparece se:
                    // - Está agendado para a data selecionada
                    // - A data selecionada É HOJE
                    // - E ainda NÃO foi completado hoje
                    val isSelectedDateToday = selectedDate.isEqual(today)

                    if (isScheduledForSelectedDate && isSelectedDateToday && !isCompletedToday) {
                        binding.btnStartHabit.visibility = View.VISIBLE
                    } else {
                        binding.btnStartHabit.visibility = View.GONE
                    }
                }
            }
        }
    }
}