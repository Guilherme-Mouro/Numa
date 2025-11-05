package com.example.numa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.numa.databinding.FragmentAddHabitBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddHabitFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)

        binding.btnSaveHabit.setOnClickListener {
            val habitName = binding.inputHabitName.text.toString()
            // Aqui você pode salvar o hábito ou enviar de volta para o fragment principal
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
