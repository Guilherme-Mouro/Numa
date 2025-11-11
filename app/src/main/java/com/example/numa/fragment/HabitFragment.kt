package com.example.numa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.numa.DataBase
import com.example.numa.adapter.HabitAdapter
import com.example.numa.adapter.WeekAdapter
import com.example.numa.databinding.FragmentHabitBinding
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HabitFragment : Fragment() {

    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DataBase
    private lateinit var habitsAdapter: HabitAdapter
    private var selectedDay: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitBinding.inflate(inflater, container, false)

        binding.btnAddHabit.setOnClickListener {
            val bottomSheet = AddHabitFragment()
            bottomSheet.show(parentFragmentManager, "AddHabitBottomSheet")
        }

        db = Room.databaseBuilder(
            requireContext(),
            DataBase::class.java,
            "NumaDB"
        ).fallbackToDestructiveMigration().build()

        binding.rvHabits.layoutManager = LinearLayoutManager(requireContext())

        val weekDays = getCurrentWeek()
        val weekAdapter = WeekAdapter(weekDays) { selectedDate ->
            selectedDay = selectedDate
            loadHabitsForDate(selectedDate)
        }
        binding.rvWeek.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeek.adapter = weekAdapter

        loadHabitsForDate(selectedDay)

        return binding.root
    }

    private fun loadHabitsForDate(date: LocalDate) {
        lifecycleScope.launch {
            val sessionManager = SessionManager(requireContext())
            val userId = sessionManager.getUserId()

            if (userId != null) {
                val dayOfWeek = date.dayOfWeek.name
                val specificDate =
                    date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

                val habits = db.habitDao().getHabitsForDate(
                    dayOfWeek = dayOfWeek,
                    specificDate = specificDate,
                    userId = userId
                )

                habitsAdapter = HabitAdapter(habits)
                binding.rvHabits.adapter = habitsAdapter
            }
        }
    }

    private fun getCurrentWeek(): List<LocalDate> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        return (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
