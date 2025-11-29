package com.example.numa.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numa.R
import com.example.numa.adapter.AchievementAdapter
import com.example.numa.adapter.ProgressQuestAdapter
import com.example.numa.adapter.Quest
import com.example.numa.databinding.FragmentQuestBinding
import com.example.numa.entity.Achievement
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.LevelUp // ✅ Importante: Importar a utilitária de Level
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class QuestFragment : Fragment() {

    private var _binding: FragmentQuestBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressAdapter: ProgressQuestAdapter
    private lateinit var achievementAdapter: AchievementAdapter

    private val sessionManager by lazy { SessionManager(requireContext()) }
    private val database by lazy { DatabaseProvider.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Configurar componentes da UI
        setupSpinner()
        setupProgressQuestRecyclerView()
        setupAchievementsRecyclerView()

        // 2. Carregar dados
        loadAchievements()

        // ✅ 3. Carregar Estatísticas do User (Level e XP)
        loadUserStats()
    }

    // ✅ NOVA FUNÇÃO: Atualiza o texto do nível e XP
    private fun loadUserStats() {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()

            if (userId != null) {
                val user = database.userDao().getUserById(userId)

                if (user != null) {
                    val xpNextLevel = LevelUp.xpForLevel(user.level)

                    // Atualiza Level
                    binding.tvUserLevel.text = "Lvl ${user.level}"

                    // ✅ Atualiza Pontos (NOVO)
                    binding.tvUserPoints.text = "${user.points} Pts"

                    // Atualiza XP
                    binding.tvUserXP.text = "${user.experience} / $xpNextLevel XP"
                }
            }
        }
    }

    private fun setupSpinner() {
        // ✅ Correção: Acede através do ID do include
        // Se o teu ficheiro spinner.xml tem um Spinner com id "spinner":
        val spinner = binding.includeSpinner.spinner

        // (Se der erro no .spinner, verifica se o ID dentro de spinner.xml é mesmo "spinner")

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
        val quests = listOf(
            Quest("Complete 3 habits", 40),
            Quest("Read 20 pages", 60),
            Quest("Exercise 30 min", 85),
            Quest("Drink 8 glasses water", 50)
        )

        progressAdapter = ProgressQuestAdapter(quests)
        binding.rvProgressQuest.apply {
            adapter = progressAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val achievements = database.achievementUserDao()
                        .getUnlockedAchievementsForUser(userId)
                    achievementAdapter.setAchievements(achievements)
                } else {
                    achievementAdapter.setAchievements(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadLockedAchievements() {
        lifecycleScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val achievements = database.achievementUserDao()
                        .getLockedAchievementsForUser(userId)
                    achievementAdapter.setAchievements(achievements)
                } else {
                    achievementAdapter.setAchievements(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showSimpleDetailsDialog(achievement: Achievement) {
        val context = requireContext()
        val message = "Level: ${achievement.level}\n\n${achievement.description}"

        AlertDialog.Builder(context)
            .setTitle(achievement.title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}