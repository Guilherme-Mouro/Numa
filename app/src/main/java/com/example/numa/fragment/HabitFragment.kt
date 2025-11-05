package com.example.numa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numa.DataBase
import com.example.numa.adapter.HabitAdapter
import com.example.numa.databinding.FragmentHabitBinding
import androidx.room.Room
import com.example.numa.adapter.WeekAdapter
import com.example.numa.dao.HabitDao
import com.example.numa.entity.Habit
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate


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


        lifecycleScope.launch {
            val sessionManager = SessionManager(requireContext())
            val userId = sessionManager.getUserId()

            userId?.let {
                val habits = db.habitDao().getHabitsByUser(userId)

                habitsAdapter = HabitAdapter(habits)
                binding.rvHabits.layoutManager = LinearLayoutManager(requireContext())
                binding.rvHabits.adapter = habitsAdapter
            }

        }

        val weekDays = getCurrentWeek()
        val weekAdapter = WeekAdapter(weekDays) { selectedDate ->
            selectedDay = selectedDate
        }

        binding.rvWeek.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeek.adapter = weekAdapter

        return binding.root
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
