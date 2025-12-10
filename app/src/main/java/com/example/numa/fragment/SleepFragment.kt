package com.example.numa.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.numa.databinding.FragmentSleepBinding
import com.example.numa.services.SleepTrackingService
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class SleepFragment : Fragment() {

    private var _binding: FragmentSleepBinding? = null
    private val binding get() = _binding!!

    private val trackingPrefs by lazy {
        requireActivity().getSharedPreferences("sleep_tracking_prefs", Context.MODE_PRIVATE)
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Todas as permissões foram concedidas
            startTrackingService()
        } else {
            Toast.makeText(requireContext(), "São necessárias permissões para monitorar o sono.", Toast.LENGTH_LONG).show()
        }
    }

    private var isTracking: Boolean
        get() = trackingPrefs.getBoolean("is_tracking", false)
        set(value) = trackingPrefs.edit().putBoolean("is_tracking", value).apply()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Atualizar a UI sempre que o fragmento se torna visível
        updateButtonUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadLatestSleepData()

        binding.btnToggleSleepTracking.setOnClickListener {
            toggleTracking(!isTracking)
        }
    }

    private fun loadLatestSleepData() {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        if (userId == null) {
            binding.tvScore.text = "--"
            binding.tvQuality.text = "Faça login"
            binding.tvDuration.text = ""
            return
        }

        lifecycleScope.launch {
            val sleepDao = DatabaseProvider.getDatabase(requireContext()).sleepDao()
            val latestSleep = sleepDao.getLatestSleepForUser(userId)

            if (latestSleep != null) {
                val durationMillis = latestSleep.endTime - latestSleep.startTime
                val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60

                binding.tvScore.text = latestSleep.score.toInt().toString()
                binding.tvQuality.text = latestSleep.quality
                binding.tvDuration.text = String.format("%dh %02dm", hours, minutes)
                binding.tvDate.text = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date(latestSleep.date))
            } else {
                binding.tvScore.text = "--"
                binding.tvQuality.text = "Sem dados"
                binding.tvDuration.text = "--"
                binding.tvDate.text = "Nenhum registo encontrado"
            }
        }
    }

    private fun toggleTracking(start: Boolean) {
        if (start) {
            checkPermissionsAndStartService()
        } else {
            stopTrackingService()
        }
    }

    private fun checkPermissionsAndStartService() {
        val requiredPermissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requiredPermissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            startTrackingService()
        } else {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun startTrackingService() {
        val intent = Intent(requireActivity(), SleepTrackingService::class.java).apply {
            action = SleepTrackingService.ACTION_START_TRACKING
        }
        requireActivity().startService(intent)
        isTracking = true
        updateButtonUI()
        Toast.makeText(requireContext(), "Monitoramento de sono iniciado.", Toast.LENGTH_SHORT).show()
    }

    private fun stopTrackingService() {
        val intent = Intent(requireActivity(), SleepTrackingService::class.java).apply {
            action = SleepTrackingService.ACTION_STOP_TRACKING
        }
        requireActivity().startService(intent)
        isTracking = false
        updateButtonUI()
        Toast.makeText(requireContext(), "Monitoramento de sono parado.", Toast.LENGTH_SHORT).show()
    }

    private fun updateButtonUI() {
        if (isTracking) {
            binding.btnToggleSleepTracking.text = "Parar Monitoramento"
        } else {
            binding.btnToggleSleepTracking.text = "Iniciar Monitoramento"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
