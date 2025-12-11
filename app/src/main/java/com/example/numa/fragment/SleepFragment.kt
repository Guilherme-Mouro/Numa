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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.numa.databinding.FragmentSleepBinding
import com.example.numa.services.SleepTrackingService
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import java.util.Calendar

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
        updateButtonUI()
        loadTodaysSleepData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnToggleSleepTracking.setOnClickListener {
            toggleTracking(!isTracking)
        }
    }

    private fun loadTodaysSleepData() {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        if (userId == null) {
            binding.rvSleepSegments.isVisible = false
            binding.tvNoData.isVisible = true
            binding.tvNoData.text = "Faça login para ver os seus registos."
            return
        }

        lifecycleScope.launch {
            val sleepDao = DatabaseProvider.getDatabase(requireContext()).sleepDao()

            // Define o período de "hoje" (ex: das 18h de ontem até agora)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 18)
            val sinceMillis = calendar.timeInMillis

            val sleepSegments = sleepDao.getSleepSegmentsSince(userId, sinceMillis)

            if (sleepSegments.isNotEmpty()) {
                binding.rvSleepSegments.adapter = SleepSegmentAdapter(sleepSegments)
                binding.rvSleepSegments.isVisible = true
                binding.tvNoData.isVisible = false
            } else {
                binding.rvSleepSegments.isVisible = false
                binding.tvNoData.isVisible = true
                binding.tvNoData.text = "Nenhum registo de sono para hoje."
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
