package com.example.numa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.numa.DataBase
import com.example.numa.databinding.FragmentAddHabitBinding
import com.example.numa.entity.Habit
import com.example.numa.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class AddHabitFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DataBase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)

        db = Room.databaseBuilder(
            requireContext(),
            DataBase::class.java,
            "NumaDB"
        ).fallbackToDestructiveMigration().build()

        binding.btnSaveHabit.setOnClickListener {
            val sessionManager = SessionManager(requireContext())
            val userId = sessionManager.getUserId()

            userId?.let {
                val newHabit = Habit(
                    userId = userId,
                    title = binding.edTitle.text.toString(),
                    description = binding.edDesc.text.toString(),
                    startTime = System.currentTimeMillis(),
                    duration = 60 * 60 * 1000L,
                    experience = 10,
                    streak = 0,
                    state = "incomplete",
                    isRecurring = true,
                    dayOfWeek = "FRIDAY",
                    specificDate = null
                )

                lifecycleScope.launch {
                    db.habitDao().insertHabit(newHabit)
                    parentFragmentManager.setFragmentResult("habit_added", Bundle())
                    dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
