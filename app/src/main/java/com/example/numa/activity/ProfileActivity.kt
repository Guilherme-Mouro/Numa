package com.example.numa.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.numa.R
import com.example.numa.databinding.ActivityProfileBinding
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.FixPixelArt
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val db by lazy { DatabaseProvider.getDatabase(this) }

    // Bluetooth
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())

    // Lista para evitar duplicados
    private val foundDevices = mutableSetOf<String>()

    // Gestor de Permissões
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                startBleScan()
            } else {
                Toast.makeText(this, "Permissões necessárias para encontrar o relógio.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Bluetooth Adapter
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Botão Voltar
        binding.btnBack.setOnClickListener {
            finish()
        }

        // --- A CORREÇÃO PRINCIPAL ESTÁ AQUI ---
        // Configurar o botão de Scan
        binding.btnScan.setOnClickListener {
            prepareAndScan()
        }

        getUserData()
    }

    private fun getUserData() {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        lifecycleScope.launch {
            userId?.let {
                val user = db.userDao().getUserById(userId)
                val pet = db.petDao().getPetByUser(userId)

                if (user != null && pet != null){
                    binding.tvName.text = user.name
                    binding.tvPoints.text = user.points.toString()
                    binding.tvStreak.text = user.streak.toString() + " days"

                    val petBanner = binding.imgPetBanner

                    petBanner.setBackgroundResource(R.drawable.cat_banner_animation)

                    FixPixelArt.removeAnimFilter(petBanner)

                    val characterAnimation = petBanner.background as AnimationDrawable
                    characterAnimation.start()

                    binding.tvPetName.text = pet.name
                    binding.tvPetHumor.text = pet.humor
                }
            }
        }
    }

    private fun prepareAndScan() {
        // 1. Verifica se o Bluetooth está ligado
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Por favor liga o Bluetooth nas definições", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Define permissões por versão do Android
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        // 3. Verifica e pede permissões
        val missingPermissions = permissionsToRequest.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            startBleScan()
        } else {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan() {
        if (isScanning) return

        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) {
            Toast.makeText(this, "Erro: Bluetooth Scanner não disponível.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- ATUALIZAR UI (Melhoria Visual) ---
        isScanning = true
        foundDevices.clear()
        binding.tvDeviceResult.text = "A procurar relógios..."
        binding.progressBar.visibility = View.VISIBLE // Mostra a rodinha
        binding.btnScan.isEnabled = false // Bloqueia o botão
        binding.btnScan.text = "A procurar..."

        // Inicia Scan
        scanner.startScan(scanCallback)

        // Pára automaticamente após 10 segundos
        handler.postDelayed({
            stopBleScan()
        }, 10000)
    }

    // Função auxiliar para parar o scan e limpar a UI
    @SuppressLint("MissingPermission")
    private fun stopBleScan() {
        if (!isScanning) return

        isScanning = false
        val scanner = bluetoothAdapter.bluetoothLeScanner
        scanner?.stopScan(scanCallback)

        // Restaurar UI
        binding.progressBar.visibility = View.GONE
        binding.btnScan.isEnabled = true
        binding.btnScan.text = "Add New Device +"

        if (foundDevices.isEmpty()) {
            binding.tvDeviceResult.text = "Nenhum relógio encontrado. Tenta aproximar."
        }
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name
            val deviceAddress = device.address

            // Filtro: Apenas dispositivos com nome e não duplicados
            if (deviceName != null && !foundDevices.contains(deviceAddress)) {
                foundDevices.add(deviceAddress)
                Log.d("NumaBluetooth", "Encontrado: $deviceName ($deviceAddress)")

                // Atualizar UI
                val currentText = binding.tvDeviceResult.text.toString()
                    .replace("A procurar relógios...", "") // Remove msg inicial
                    .replace("Nenhum relógio encontrado. Tenta aproximar.", "")

                binding.tvDeviceResult.text = "$deviceName\n$currentText"
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("NumaBluetooth", "Scan falhou: $errorCode")
            stopBleScan() // Garante que a UI volta ao normal se falhar
        }
    }
}