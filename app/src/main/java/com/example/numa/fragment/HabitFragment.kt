package com.example.numa.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.numa.DataBase
import com.example.numa.R
import com.example.numa.adapter.HabitAdapter
import com.example.numa.adapter.WeekAdapter
import com.example.numa.databinding.FragmentHabitBinding
import com.example.numa.entity.Habit
import com.example.numa.util.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        parentFragmentManager.setFragmentResultListener("habit_request", viewLifecycleOwner) { _, _ ->
            loadHabitsForDate(selectedDay)
            loadHabitsProgress()
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

        loadHabitsProgress()

        loadHabitsForDate(selectedDay)
        setupSwipeToDelete()

        return binding.root
    }

    private fun loadHabitsProgress() {
        lifecycleScope.launch {
            val sessionManager = SessionManager(requireContext())
            val userId = sessionManager.getUserId()

            val today = LocalDate.now().dayOfWeek.name

            userId?.let {
                val habits = db.habitDao().getHabitsForDate(today, 1, userId)

                val totalHabits = habits.size
                var totalCompletedHabits = 0

                for(habit in habits) {
                    if (habit.state == "completed") {
                        totalCompletedHabits++
                    }
                }

                val habitsProgress = totalCompletedHabits * 100 / totalHabits

                val green = ContextCompat.getColor(requireContext(), R.color.green)
                val greenBar = ContextCompat.getDrawable(requireContext(), R.drawable.bg_progress_bar_completed)

                val yellow = ContextCompat.getColor(requireContext(), R.color.yellow)
                val yellowBar = ContextCompat.getDrawable(requireContext(), R.drawable.bg_progress_bar)


                if (habitsProgress == 100) {
                    binding.tvPercentage.setTextColor(green)
                    binding.cardProgress.setStrokeColor(green)
                    binding.progressBar.setProgressDrawableTiled(greenBar)
                } else {
                    binding.tvPercentage.setTextColor(yellow)
                    binding.cardProgress.setStrokeColor(yellow)
                    binding.progressBar.setProgressDrawableTiled(yellowBar)
                }

                binding.progressBar.progress = habitsProgress
                binding.tvPercentage.text = "$habitsProgress%"
                binding.tvHabitsNumber.text = "$totalCompletedHabits of $totalHabits habits completed"
            }
        }
    }

    private fun loadHabitsForDate(date: LocalDate) {
        lifecycleScope.launch {
            val sessionManager = SessionManager(requireContext())
            val userId = sessionManager.getUserId()

            userId?.let {
                val dayOfWeek = date.dayOfWeek.name
                val specificDate =
                    date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

                val habits = db.habitDao().getHabitsForDate(
                    dayOfWeek = dayOfWeek,
                    specificDate = specificDate,
                    userId = userId
                )

                if (habits.isEmpty()) {
                    binding.tvInfo.visibility = View.VISIBLE
                } else {
                    binding.tvInfo.visibility = View.INVISIBLE
                }

                habitsAdapter = HabitAdapter(habits.toMutableList())
                binding.rvHabits.adapter = habitsAdapter
            }
        }
    }

    private fun getCurrentWeek(): List<LocalDate> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        return (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val habit: Habit = habitsAdapter.getHabitAt(position)

                    habitsAdapter.removeItem(position)

                    Snackbar.make(binding.root, "Habit Removed", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            habitsAdapter.habits.add(position, habit)
                            habitsAdapter.notifyItemInserted(position)
                        }
                        .addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                if (event != DISMISS_EVENT_ACTION) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        db.habitDao().deleteHabit(habit)
                                        loadHabitsProgress()
                                    }
                                }
                            }
                        })
                        .show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvHabits)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
