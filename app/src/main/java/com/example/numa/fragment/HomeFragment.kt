package com.example.numa.fragment

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numa.activity.HabitActivity
import com.example.numa.adapter.HabitAdapter
import com.example.numa.databinding.FragmentHomeBinding
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.FixPixelArt
import com.example.numa.util.SessionManager
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }
    private lateinit var habitsAdapter: HabitAdapter

    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // This prevents the 'ZoneRulesException' crash when using LocalDate
        AndroidThreeTen.init(requireContext())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Creating the sessionManager variable to be able to check the user Id later
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        loadPet(userId)
        loadHabits(selectedDate, userId) // ✅ Passa a data selecionada

        return binding.root
    }

    private fun loadPet(userId: Int?) {
        lifecycleScope.launch {
            userId?.let {
                val pet = db.petDao().getPetByUser(userId)

                if (pet != null) {
                    val petSprite = binding.imgPet
                    val petSkin = resources.getIdentifier(pet.skin + "idle_animation", "drawable", requireContext().packageName)

                    if (petSkin != 0) {
                        petSprite.setBackgroundResource(petSkin)
                        FixPixelArt.removeAnimFilter(petSprite)

                        val background = petSprite.background
                        if (background is AnimationDrawable) {
                            background.start()
                        }
                    }
                }
            }
        }
    }

    private fun loadHabits(date: LocalDate, userId: Int?) {
        lifecycleScope.launch {

            val uid = userId ?: return@launch

            val dayOfWeek = date.dayOfWeek.name
            val specificDate = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

            val selectedDayStart = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

            val habitsForDate = db.habitDao().getHabitsForDate(
                dayOfWeek,
                specificDate,
                uid,
                selectedDayStart
            )

            val habitsCompleted = db.habitDao().getCompletedHabitsForDate(
                dayOfWeek,
                specificDate,
                uid,
                selectedDayStart
            )

            val titleHabits = binding.tvHabitsTitle
            val titleCompleted = binding.tvCompletedHabitsTitle

            if (habitsForDate.isEmpty() && habitsCompleted.isEmpty()) {
                titleHabits.visibility = View.GONE
                titleCompleted.visibility = View.GONE
                binding.tvInfo.visibility = View.VISIBLE
                return@launch
            } else {
                binding.tvInfo.visibility = View.GONE
            }

            if (habitsForDate.isEmpty()) {
                titleHabits.visibility = View.GONE
            } else {
                titleHabits.visibility = View.VISIBLE
                binding.rvHabits.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = HabitAdapter(
                        habitsForDate.toMutableList(),
                        { habit ->
                            val intent = Intent(requireContext(), HabitActivity::class.java)
                            intent.putExtra("habitId", habit.id)
                            startActivity(intent)
                        },
                        date
                    )
                }
            }

            if (habitsCompleted.isEmpty()) {
                titleCompleted.visibility = View.GONE
            } else {
                titleCompleted.visibility = View.VISIBLE
                binding.rvCompletedHabits.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = HabitAdapter(
                        habitsCompleted.toMutableList(),
                        { habit -> },
                        date
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}