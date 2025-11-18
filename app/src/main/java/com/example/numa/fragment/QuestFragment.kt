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
import com.example.numa.util.DatabaseProvider
import com.example.numa.entity.Achievement
import com.example.numa.util.SessionManager

class QuestFragment : Fragment() {

    private lateinit var binding: FragmentQuestBinding
    private lateinit var progressAdapter: ProgressQuestAdapter
    private lateinit var achievementAdapter: AchievementAdapter

    // ✅ CORREÇÃO: Inicializa o SessionManager para ser usado nos métodos load
    private val sessionManager by lazy { SessionManager(requireContext()) }

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
        // Inicializa o Adapter e define a ação de clique para mostrar o AlertDialog
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
        // Por padrão, carrega todos os achievements na inicialização
        loadAllAchievements()
    }

    private fun loadAllAchievements() {
        lifecycleScope.launch {
            try {
                // Carrega todos, independentemente do usuário
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
                // Obtém o ID do usuário da sessão
                val userId = sessionManager.getUserId()

                if (userId != null) {
                    val achievements = database.achievementUserDao()
                        .getUnlockedAchievementsForUser(userId)
                    achievementAdapter.setAchievements(achievements)
                } else {
                    // Limpar ou mostrar uma mensagem de erro se o ID não for encontrado
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
                // Obtém o ID do usuário da sessão
                val userId = sessionManager.getUserId()

                if (userId != null) {
                    val achievements = database.achievementUserDao()
                        .getLockedAchievementsForUser(userId)
                    achievementAdapter.setAchievements(achievements)
                } else {
                    // Limpar ou mostrar uma mensagem de erro se o ID não for encontrado
                    achievementAdapter.setAchievements(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Criar a função simples para mostrar os detalhes (AlertDialog)
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