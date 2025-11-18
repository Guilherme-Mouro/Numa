package com.example.numa.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import com.example.numa.R
import com.example.numa.adapter.ProgressQuestAdapter
import com.example.numa.adapter.AchievementAdapter
import com.example.numa.adapter.Quest
import com.example.numa.databinding.FragmentQuestBinding
import com.example.numa.DatabaseProvider
import com.example.numa.entity.Achievement

class QuestFragment : Fragment() {

    private lateinit var binding: FragmentQuestBinding
    private lateinit var progressAdapter: ProgressQuestAdapter
    private lateinit var achievementAdapter: AchievementAdapter

    private val database by lazy { DatabaseProvider.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar o spinner
        setupSpinner()

        // RecyclerView de Quests
        setupProgressQuestRecyclerView()

        // RecyclerView de Achievements
        setupAchievementsRecyclerView()

        // Carregar achievements
        loadAchievements()
    }

    private fun setupSpinner() {
        val spinner = binding.root.findViewById<Spinner>(R.id.spinner)
        val items = arrayOf("See All", "Desbloqueados", "Bloqueados")

        val adapterSpinner = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                return view
            }
        }

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        // Listener para mudar achievements quando spinner muda
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> loadAllAchievements()
                    1 -> loadUnlockedAchievements()
                    2 -> loadLockedAchievements()
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupProgressQuestRecyclerView() {
        // Dados de exemplo
        val quests = listOf(
            Quest("Complete 3 habits", 40),
            Quest("Read 20 pages", 60),
            Quest("Exercise 30 min", 85),
            Quest("Drink 8 glasses water", 50)
        )

        progressAdapter = ProgressQuestAdapter(quests)
        binding.rvProgressQuest.apply {
            adapter = progressAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        }
    }

    private fun setupAchievementsRecyclerView() {
        achievementAdapter = AchievementAdapter { achievement ->
            showSimpleDetailsDialog(achievement)
        }

        binding.rvAchievements.apply {
            adapter = achievementAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadAchievements() {
        loadAllAchievements()
    }

    private fun loadAllAchievements() {
        lifecycleScope.launch {
            try {
                val achievements = database.achievementDao().getAllAchievements()
                achievementAdapter.setAchievements(achievements)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadUnlockedAchievements() {
        lifecycleScope.launch {
            try {
                val userId = 1  // Ou pega do teu user atual
                val achievements = database.achievementUserDao()
                    .getUnlockedAchievementsForUser(userId)
                achievementAdapter.setAchievements(achievements)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadLockedAchievements() {
        lifecycleScope.launch {
            try {
                val userId = 1  // Ou pega do teu user atual
                val achievements = database.achievementUserDao()
                    .getLockedAchievementsForUser(userId)
                achievementAdapter.setAchievements(achievements)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Criar a função simples para mostrar os detalhes
    private fun showSimpleDetailsDialog(achievement: Achievement) {
        val context = requireContext()

        // Conteúdo da mensagem
        val message = "Level: ${achievement.level}\n\n${achievement.description}"

        AlertDialog.Builder(context)
            .setTitle(achievement.title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}