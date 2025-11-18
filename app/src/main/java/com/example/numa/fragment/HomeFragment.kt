package com.example.numa.fragment

import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.numa.DataBase
import com.example.numa.R
import com.example.numa.adapter.HabitAdapter
import com.example.numa.databinding.FragmentHomeBinding
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DataBase
    private lateinit var habitsAdapter: HabitAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Creating the sessionManager variable to be able to check the user Id later
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        db = Room.databaseBuilder(
            requireContext(),
            DataBase::class.java,
            "NumaDB"
        ).fallbackToDestructiveMigration().build()

        val characterImageView = binding.character

        characterImageView.setBackgroundResource(R.drawable.char_animation)

        val characterAnimation = characterImageView.background as AnimationDrawable
        characterAnimation.start()

        loadIncompletedHabits(userId)
        loadCompletedHabits(userId)

        return binding.root
    }

    private fun loadIncompletedHabits(userId: Int?) {
        lifecycleScope.launch {
            val today = LocalDate.now().dayOfWeek.name
            val specificDate =
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

            userId?.let {
                val habits = db.habitDao().getHabitsForDate(today, specificDate, userId)

                habitsAdapter = HabitAdapter(habits.toMutableList())
                binding.rvHabits.layoutManager = LinearLayoutManager(requireContext())
                binding.rvHabits.adapter = habitsAdapter

            }
        }
    }

    private fun loadCompletedHabits(userId: Int?) {
        lifecycleScope.launch {
            val today = LocalDate.now().dayOfWeek.name
            val specificDate =
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

            userId?.let {
                val habits = db.habitDao().getCompletedHabitsForDate(today, specificDate, userId)

                habitsAdapter = HabitAdapter(habits.toMutableList())
                binding.rvCompletedHabits.layoutManager = LinearLayoutManager(requireContext())
                binding.rvCompletedHabits.adapter = habitsAdapter

            }
        }
    }

}