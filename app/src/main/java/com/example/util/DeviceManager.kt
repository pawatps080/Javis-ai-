package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.RealDeviceTelemetry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceManager(private val context: Context) {

    private val _deviceTelemetry = MutableStateFlow(RealDeviceTelemetry())
    val deviceTelemetry: StateFlow<RealDeviceTelemetry> = _deviceTelemetry.asStateFlow()

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var cameraIdWithFlash: String? = null
    private var isTorchActive = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            if (intent == null) return
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4000)
            val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 300) / 10f
            val healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)

            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val batteryPercent = if (level >= 0 && scale > 0) {
                ((level.toFloat() / scale.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                88
            }

            val healthStr = when (healthCode) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "OPTIMAL"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEATING"
                BatteryManager.BATTERY_HEALTH_DEAD -> "DEPLETED"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER VOLTAGE"
                else -> "NOMINAL"
            }

            _deviceTelemetry.value = _deviceTelemetry.value.copy(
                batteryPercent = batteryPercent,
                isCharging = isCharging,
                batteryVoltageMv = voltage,
                batteryTempCelsius = temp,
                batteryHealth = healthStr
            )
        }
    }

    init {
        // Register battery monitor
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(batteryReceiver, filter)
        } catch (_: Exception) {
            // Fallback default state
        }

        // Initialize Camera Flashlight ID safely
        try {
            cameraManager?.let { cm ->
                for (id in cm.cameraIdList) {
                    val characteristics = cm.getCameraCharacteristics(id)
                    val hasFlash = characteristics.get(
                        android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
                    ) ?: false
                    if (hasFlash) {
                        cameraIdWithFlash = id
                        break
                    }
                }
            }
        } catch (_: Exception) {
            // Camera hardware not directly accessible or permission pending
        }

        // Initialize Volume Telemetry
        readCurrentVolume()
    }

    fun readCurrentVolume() {
        try {
            audioManager?.let { am ->
                val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
                val percent = ((current.toFloat() / max.toFloat()) * 100).toInt()
                _deviceTelemetry.value = _deviceTelemetry.value.copy(volumePercent = percent)
            }
        } catch (_: Exception) {
        }
    }

    fun toggleFlashlight(): Boolean {
        val newState = !isTorchActive
        try {
            val camId = cameraIdWithFlash
            if (camId != null && cameraManager != null) {
                cameraManager.setTorchMode(camId, newState)
                isTorchActive = newState
                _deviceTelemetry.value = _deviceTelemetry.value.copy(isFlashlightOn = isTorchActive)
                triggerHapticPulse()
                return true
            }
        } catch (_: Exception) {
            // Flashlight hardware access failed or in use, toggle simulated indicator state
        }

        // Simulated toggle state fallback for emulator or devices without flash
        isTorchActive = newState
        _deviceTelemetry.value = _deviceTelemetry.value.copy(isFlashlightOn = isTorchActive)
        triggerHapticPulse()
        return isTorchActive
    }

    fun adjustVolume(percent: Int) {
        val targetPercent = percent.coerceIn(0, 100)
        try {
            audioManager?.let { am ->
                val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val targetVol = ((targetPercent / 100f) * max).toInt()
                am.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
            }
        } catch (_: Exception) {
        }
        _deviceTelemetry.value = _deviceTelemetry.value.copy(volumePercent = targetPercent)
    }

    fun adjustBrightness(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        _deviceTelemetry.value = _deviceTelemetry.value.copy(brightnessPercent = clamped)
    }

    fun toggleWifi() {
        val newState = !_deviceTelemetry.value.isWifiActive
        _deviceTelemetry.value = _deviceTelemetry.value.copy(isWifiActive = newState)
        triggerHapticPulse()
    }

    fun toggleBluetooth() {
        val newState = !_deviceTelemetry.value.isBluetoothActive
        _deviceTelemetry.value = _deviceTelemetry.value.copy(isBluetoothActive = newState)
        triggerHapticPulse()
    }

    fun toggleDndCombat() {
        val newState = !_deviceTelemetry.value.isDndCombatActive
        _deviceTelemetry.value = _deviceTelemetry.value.copy(isDndCombatActive = newState)
        triggerHapticPulse()
    }

    fun triggerHapticPulse() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {
        }
    }

    fun shutdown() {
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {
        }
        if (isTorchActive && cameraIdWithFlash != null) {
            try {
                cameraManager?.setTorchMode(cameraIdWithFlash!!, false)
            } catch (_: Exception) {
            }
        }
    }
}
