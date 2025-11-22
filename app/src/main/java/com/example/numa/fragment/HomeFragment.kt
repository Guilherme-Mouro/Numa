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
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.FixPixelArt
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }
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

        FixPixelArt(requireContext()).removeFilter(binding.imgBackground)

        val characterImageView = binding.character

        characterImageView.setBackgroundResource(R.drawable.char_animation)

        val characterAnimation = characterImageView.background as AnimationDrawable
        characterAnimation.start()

        loadHabits(userId)

        return binding.root
    }

    private fun loadHabits(userId: Int?) {
        lifecycleScope.launch {

            val uid = userId ?: return@launch

            val today = LocalDate.now().dayOfWeek.name
            val specificDate =
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

            val habitsForDate = db.habitDao().getHabitsForDate(today, specificDate, uid)
            val habitsCompleted = db.habitDao().getCompletedHabitsForDate(today, specificDate, uid)

            val titleHabits = binding.tvHabitsTitle
            val titleCompleted = binding.tvCompletedHabitsTitle

            if (habitsForDate.isEmpty() && habitsCompleted.isEmpty()) {
                titleHabits.visibility = View.GONE
                titleCompleted.visibility = View.GONE
                binding.tvInfo.visibility = View.VISIBLE
                return@launch
            }

            if (habitsForDate.isEmpty()) {
                titleHabits.visibility = View.GONE
            } else {
                titleHabits.visibility = View.VISIBLE
                binding.rvHabits.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = HabitAdapter(habitsForDate.toMutableList())
                }
            }

            if (habitsCompleted.isEmpty()) {
                titleCompleted.visibility = View.GONE
            } else {
                titleCompleted.visibility = View.VISIBLE
                binding.rvCompletedHabits.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = HabitAdapter(habitsCompleted.toMutableList())
                }
            }
        }
    }

}