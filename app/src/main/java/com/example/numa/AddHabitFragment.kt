package com.example.numa.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.numa.DataBase
import com.example.numa.databinding.FragmentAddHabitBinding
import com.example.numa.entity.Habit
import com.example.numa.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        val weekList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDayWeek.adapter = arrayAdapter

        binding.cbRecurring.setOnCheckedChangeListener { _, _ ->
            isRecurring()
        }

        binding.btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    binding.btnSelectDate.text = "$d/${m + 1}/$y"
                }, year, month, day
            )

            datePicker.show()
        }

        val durPicker = binding.numDuration
        durPicker.minValue = 1
        durPicker.maxValue = 300
        durPicker.value = 10

        val timePicked = binding.timeStart
        timePicked.setIs24HourView(true)

        binding.btnSaveHabit.setOnClickListener {
            val sessionManager = SessionManager(requireContext())
            val userId = sessionManager.getUserId()

            userId?.let {
                val title = binding.edTitle.text.toString().trim()
                val description = binding.edDesc.text.toString().trim()
                val recurring = binding.cbRecurring.isChecked
                val duration: Long = durPicker.value * 60 * 1000L
                var dayOfWeek: String? = null
                var specificDate: Long? = null

                if (recurring) {
                    dayOfWeek = binding.spDayWeek.selectedItem.toString().uppercase()
                } else {
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    specificDate = format.parse(binding.btnSelectDate.text.toString())?.time ?: 0L
                }

                val hour = timePicked.hour
                val min = timePicked.minute
                val startTime: Long = (hour * 60 * 60 * 1000 + min * 60 * 1000).toLong()

                if (title.isNotEmpty()) {
                    val newHabit = Habit(
                        userId = userId,
                        title = title,
                        description = description,
                        startTime = startTime,
                        duration = duration,
                        experience = 10,
                        streak = 0,
                        state = "incomplete",
                        isRecurring = recurring,
                        dayOfWeek = dayOfWeek,
                        specificDate = specificDate,
                    )

                    lifecycleScope.launch(Dispatchers.IO) {
                        db.habitDao().insertHabit(newHabit)

                        withContext(Dispatchers.Main) {
                            parentFragmentManager.setFragmentResult("habit_request", Bundle())
                            dismiss()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun isRecurring() {
        if (binding.cbRecurring.isChecked) {
            binding.spDayWeek.visibility = View.VISIBLE
            binding.btnSelectDate.visibility = View.INVISIBLE
        } else {
            binding.spDayWeek.visibility = View.INVISIBLE
            binding.btnSelectDate.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
