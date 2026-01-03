package com.example.numa.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val db by lazy { DatabaseProvider.getDatabase(this) }

    // Bluetooth
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())
    private var bluetoothGatt: BluetoothGatt? = null

    // Lista para evitar duplicados visualmente
    private val foundDevicesAddresses = mutableSetOf<String>()

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

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnScan.setOnClickListener { prepareAndScan() }

        getUserData()
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
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
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Liga o Bluetooth primeiro!", Toast.LENGTH_SHORT).show()
            return
        }

        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        }

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
            Toast.makeText(this, "Bluetooth não disponível.", Toast.LENGTH_SHORT).show()
            return
        }

        // Reset da UI e Conexões
        bluetoothGatt?.close()
        bluetoothGatt = null

        // LIMPEZA IMPORTANTE: Removemos os botões da lista anterior
        binding.llDeviceList.removeAllViews()
        foundDevicesAddresses.clear()

        isScanning = true
        binding.progressBar.visibility = View.VISIBLE
        binding.btnScan.isEnabled = false
        binding.btnScan.text = "A procurar..."

        scanner.startScan(scanCallback)

        handler.postDelayed({ stopBleScan() }, 10000)
    }

    @SuppressLint("MissingPermission")
    private fun stopBleScan() {
        if (!isScanning) return

        isScanning = false
        val scanner = bluetoothAdapter.bluetoothLeScanner
        scanner?.stopScan(scanCallback)

        // Se não conectou a ninguém, restaura a UI
        if (bluetoothGatt == null) {
            binding.progressBar.visibility = View.GONE
            binding.btnScan.isEnabled = true
            binding.btnScan.text = "Add New Device +"

            if (foundDevicesAddresses.isEmpty()) {
                addMessageToLayout("Nenhum dispositivo encontrado.")
            }
        }
    }

    // Função auxiliar para adicionar texto simples à lista
    private fun addMessageToLayout(message: String) {
        val textView = TextView(this)
        textView.text = message
        textView.setTextColor(Color.WHITE)
        textView.setPadding(16, 16, 16, 16)
        binding.llDeviceList.addView(textView)
    }

    // --- LÓGICA DE CONEXÃO AO CLICAR ---
    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        // Parar scan antes de conectar
        stopBleScan()

        binding.progressBar.visibility = View.VISIBLE
        binding.btnScan.text = "A conectar..."

        // Limpar a lista visual e mostrar apenas "A conectar..."
        binding.llDeviceList.removeAllViews()
        addMessageToLayout("A conectar a ${device.name ?: "Dispositivo"}...")

        Log.d("NumaBluetooth", "A conectar a ${device.address}")
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name
            val deviceAddress = device.address

            // Filtro: Se tem nome e ainda não está na lista
            if (deviceName != null && !foundDevicesAddresses.contains(deviceAddress)) {
                foundDevicesAddresses.add(deviceAddress)

                // CRIAÇÃO DO BOTÃO DINÂMICO
                // Criamos um botão para cada relógio encontrado
                val deviceButton = Button(this@ProfileActivity)
                deviceButton.text = "$deviceName\n$deviceAddress"
                deviceButton.textSize = 14f
                deviceButton.isAllCaps = false

                // Estilo simples para ficar bonito na lista
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 8, 0, 8)
                deviceButton.layoutParams = params

                // O QUE ACONTECE QUANDO CLICAS NELE:
                deviceButton.setOnClickListener {
                    connectToDevice(device)
                }

                // Adiciona o botão ao layout
                binding.llDeviceList.addView(deviceButton)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("NumaBluetooth", "Scan falhou: $errorCode")
            stopBleScan()
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread {
                    binding.llDeviceList.removeAllViews()
                    addMessageToLayout("Conectado! A descobrir serviços...")
                }
                gatt.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.btnScan.isEnabled = true
                    binding.btnScan.text = "Add New Device +"
                    binding.llDeviceList.removeAllViews()
                    addMessageToLayout("Desconectado.")
                }
                bluetoothGatt = null
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val batteryServiceUuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
                val batteryLevelUuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")

                val service = gatt.getService(batteryServiceUuid)
                val characteristic = service?.getCharacteristic(batteryLevelUuid)

                if (characteristic != null) {
                    gatt.readCharacteristic(characteristic)
                } else {
                    runOnUiThread {
                        binding.llDeviceList.removeAllViews()
                        addMessageToLayout("Conectado!\n(Bateria não acessível)")
                        binding.progressBar.visibility = View.GONE
                        binding.btnScan.text = "Connected"
                    }
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            handleBatteryRead(characteristic)
        }

        // Compatibilidade Android antigo
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            handleBatteryRead(characteristic)
        }

        private fun handleBatteryRead(characteristic: BluetoothGattCharacteristic) {
            val batteryLevel = characteristic.value.firstOrNull()?.toInt() ?: 0
            runOnUiThread {
                binding.llDeviceList.removeAllViews()
                addMessageToLayout("Conectado!\nBateria: $batteryLevel%")
                binding.progressBar.visibility = View.GONE
                binding.btnScan.text = "Connected"
            }
        }
    }
}