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
import com.example.numa.dao.HabitDao
import com.example.numa.entity.Habit
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch


class HabitFragment : Fragment() {

    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DataBase

    private lateinit var habitsAdapter: HabitAdapter


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

            if (userId != null) {
                val habits = db.habitDao().getHabitsByUser(userId)

                habitsAdapter = HabitAdapter(habits)
                binding.rvHabits.layoutManager = LinearLayoutManager(requireContext())
                binding.rvHabits.adapter = habitsAdapter
            }

        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
