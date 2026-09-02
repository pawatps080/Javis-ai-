package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.DefaultMarketData
import com.example.data.DefaultWeatherData
import com.example.data.JarvisPersona
import com.example.data.JarvisRepository
import com.example.data.LightingPreset
import com.example.data.MarketAsset
import com.example.data.MarketCategory
import com.example.data.MessageSender
import com.example.data.RealDeviceTelemetry
import com.example.data.SecurityDefenseMode
import com.example.data.SmartHomeState
import com.example.data.StarkWeatherLocation
import com.example.util.DeviceManager
import com.example.util.SpeechManager
import com.example.util.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SuitDiagnostics(
    val arcReactorOutput: Float = 98.4f,
    val coreTempKelvin: Int = 4120,
    val repulsorCharge: Float = 100.0f,
    val nanotechIntegrity: Float = 100.0f,
    val machVelocity: Float = 0.0f,
    val defenseShield: Float = 95.0f,
    val thrusterEfficiency: Float = 99.2f,
    val activeProtocols: List<String> = listOf("STARK_NET_SECURE", "MARK_LXXXV_MAIN", "FLIGHT_STABILIZER_V4")
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JarvisRepository()
    private val ttsManager = TtsManager(application)
    private val speechManager = SpeechManager(application)
    private val deviceManager = DeviceManager(application)

    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val isMuted: StateFlow<Boolean> = ttsManager.isMuted

    val isListening: StateFlow<Boolean> = speechManager.isListening
    val audioLevel: StateFlow<Float> = speechManager.audioLevel
    val recognizedText: StateFlow<String> = speechManager.recognizedText
    val speechError: StateFlow<String?> = speechManager.speechError

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _persona = MutableStateFlow(JarvisPersona.STARK_BUTLER)
    val persona: StateFlow<JarvisPersona> = _persona.asStateFlow()

    private val _diagnostics = MutableStateFlow(SuitDiagnostics())
    val diagnostics: StateFlow<SuitDiagnostics> = _diagnostics.asStateFlow()

    private val _scannedImage = MutableStateFlow<Bitmap?>(null)
    val scannedImage: StateFlow<Bitmap?> = _scannedImage.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // MARKETS & CRYPTO STATE
    private val _marketAssets = MutableStateFlow(DefaultMarketData.getInitialAssets())
    val marketAssets: StateFlow<List<MarketAsset>> = _marketAssets.asStateFlow()

    private val _selectedMarketCategory = MutableStateFlow(MarketCategory.ALL)
    val selectedMarketCategory: StateFlow<MarketCategory> = _selectedMarketCategory.asStateFlow()

    private val _isMarketsRefreshing = MutableStateFlow(false)
    val isMarketsRefreshing: StateFlow<Boolean> = _isMarketsRefreshing.asStateFlow()

    // WEATHER & ATMOSPHERIC STATE
    private val _weatherLocations = MutableStateFlow(DefaultWeatherData.getInitialLocations())
    val weatherLocations: StateFlow<List<StarkWeatherLocation>> = _weatherLocations.asStateFlow()

    private val _selectedWeatherLocationIndex = MutableStateFlow(0)
    val selectedWeatherLocationIndex: StateFlow<Int> = _selectedWeatherLocationIndex.asStateFlow()

    // DEVICE & SMART HOME STATE
    val deviceTelemetry: StateFlow<RealDeviceTelemetry> = deviceManager.deviceTelemetry

    private val _smartHomeState = MutableStateFlow(SmartHomeState())
    val smartHomeState: StateFlow<SmartHomeState> = _smartHomeState.asStateFlow()

    init {
        speechManager.onResultListener = { command ->
            sendUserMessage(command)
        }

        // Welcome greeting from J.A.R.V.I.S.
        initJarvisWelcome()
    }

    private fun initJarvisWelcome() {
        val welcomeMsg = "Good day, sir. All Mark LXXXV systems, global market feeds, weather stations, and mansion automation arrays are online. How may I be of assistance?"
        _messages.value = listOf(
            ChatMessage(
                sender = MessageSender.JARVIS,
                text = welcomeMsg,
                telemetryTag = "SYSTEM INITIALIZED"
            )
        )
        viewModelScope.launch {
            delay(400)
            ttsManager.speak(welcomeMsg)
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setPersona(newPersona: JarvisPersona) {
        _persona.value = newPersona
        val confirmation = "Protocol adjusted, sir. Personality matrix updated to ${newPersona.title}."
        _messages.value = _messages.value + ChatMessage(
            sender = MessageSender.SYSTEM,
            text = confirmation,
            telemetryTag = "PROTOCOL UPDATE"
        )
        ttsManager.speak(confirmation)
    }

    fun toggleMute() {
        ttsManager.toggleMute()
    }

    fun toggleVoiceListening() {
        if (speechManager.isListening.value) {
            speechManager.stopListening()
        } else {
            ttsManager.stop()
            speechManager.startListening()
        }
    }

    fun sendUserMessage(text: String, image: Bitmap? = null) {
        if (text.isBlank() && image == null) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = text.ifBlank { "Analyze this visual schematic, Jarvis." },
            imageBitmap = image,
            telemetryTag = "VOICE/INPUT"
        )
        _messages.value = _messages.value + userMessage
        _isThinking.value = true

        viewModelScope.launch {
            try {
                val reply = repository.sendMessage(
                    history = _messages.value,
                    newPrompt = text,
                    persona = _persona.value,
                    image = image
                )
                _isThinking.value = false
                val jarvisMessage = ChatMessage(
                    sender = MessageSender.JARVIS,
                    text = reply,
                    telemetryTag = if (image != null) "OPTICAL ANALYSIS" else "AI SYNTHESIS"
                )
                _messages.value = _messages.value + jarvisMessage
                ttsManager.speak(reply)
            } catch (e: Exception) {
                _isThinking.value = false
                val errorMsg = "Apologies, sir. An anomaly occurred in the neural network: ${e.localizedMessage}"
                _messages.value = _messages.value + ChatMessage(
                    sender = MessageSender.JARVIS,
                    text = errorMsg,
                    telemetryTag = "ERROR"
                )
                ttsManager.speak(errorMsg)
            }
        }
    }

    fun runDiagnosticCycle() {
        viewModelScope.launch {
            _isThinking.value = true
            val initialNotice = ChatMessage(
                sender = MessageSender.SYSTEM,
                text = "Initiating comprehensive diagnostics for Mark LXXXV Armor...",
                telemetryTag = "DIAG_SEQ_01"
            )
            _messages.value = _messages.value + initialNotice
            ttsManager.speak("Initiating diagnostic cycle, sir. Checking primary Arc Reactor and Repulsor capacitors.")

            delay(1200)
            _diagnostics.value = _diagnostics.value.copy(
                arcReactorOutput = (97.8f + (Math.random() * 2f).toFloat()).coerceAtMost(100f),
                coreTempKelvin = 4120 + (Math.random() * 80).toInt(),
                defenseShield = (94f + (Math.random() * 6f).toFloat()).coerceAtMost(100f),
                repulsorCharge = 100.0f
            )

            val reply = "Diagnostic check complete, sir. Primary Arc Reactor running at ${_diagnostics.value.arcReactorOutput}%. Core temperature is optimal. Nanotech matrix is at 100% integrity."
            _isThinking.value = false
            _messages.value = _messages.value + ChatMessage(
                sender = MessageSender.JARVIS,
                text = reply,
                telemetryTag = "DIAGNOSTICS COMPLETE"
            )
            ttsManager.speak(reply)
        }
    }

    fun triggerProtocol(protocolName: String) {
        val prompt = when (protocolName) {
            "HOUSE_PARTY" -> "Initiate House Party Protocol."
            "CLEAN_SLATE" -> "Prepare Clean Slate Protocol."
            "VERONICA" -> "Arm Veronica orbital Hulkbuster deployment."
            "IGOR_LIFT" -> "Deploy Mark XXXVIII Igor for heavy structural support."
            "STEALTH_MODE" -> "Engage Mark XV Sneaky cloaking and radar dampening."
            else -> "Execute protocol $protocolName"
        }
        sendUserMessage(prompt)
    }

    fun setScannedImage(bitmap: Bitmap?) {
        _scannedImage.value = bitmap
    }

    fun clearHistory() {
        ttsManager.stop()
        initJarvisWelcome()
    }

    // MARKETS & CRYPTO ACTIONS
    fun setMarketCategory(category: MarketCategory) {
        _selectedMarketCategory.value = category
    }

    fun refreshMarketData() {
        viewModelScope.launch {
            _isMarketsRefreshing.value = true
            delay(600)
            _marketAssets.value = _marketAssets.value.map { asset ->
                val deltaPercent = ((Math.random() * 1.2) - 0.5)
                val newPrice = (asset.price * (1 + deltaPercent / 100)).coerceAtLeast(1.0)
                val newSparkline = asset.sparkline.drop(1) + newPrice.toFloat()
                asset.copy(
                    price = newPrice,
                    changePercent = asset.changePercent + deltaPercent,
                    sparkline = newSparkline
                )
            }
            _isMarketsRefreshing.value = false
            ttsManager.speak("Global financial indices and crypto feeds refreshed, sir.")
        }
    }

    // WEATHER ACTIONS
    fun selectWeatherLocation(index: Int) {
        if (index in _weatherLocations.value.indices) {
            _selectedWeatherLocationIndex.value = index
            val loc = _weatherLocations.value[index]
            ttsManager.speak("Atmospheric telemetry synchronized with ${loc.name}, sir.")
        }
    }

    fun toggleWeatherShield() {
        val idx = _selectedWeatherLocationIndex.value
        val list = _weatherLocations.value.toMutableList()
        val current = list[idx]
        val newState = !current.isShieldActive
        list[idx] = current.copy(isShieldActive = newState)
        _weatherLocations.value = list
        val msg = if (newState) "Precipitation dissipation shield engaged for ${current.name}." else "Precipitation shield deactivated."
        ttsManager.speak(msg)
    }

    fun toggleCloudSeeding() {
        val idx = _selectedWeatherLocationIndex.value
        val list = _weatherLocations.value.toMutableList()
        val current = list[idx]
        val newState = !current.isCloudSeedingActive
        list[idx] = current.copy(isCloudSeedingActive = newState)
        _weatherLocations.value = list
        val msg = if (newState) "Ozone cloud seeding flares deployed, sir." else "Cloud seeding protocol terminated."
        ttsManager.speak(msg)
    }

    fun adjustMicroClimateTemp(delta: Float) {
        val idx = _selectedWeatherLocationIndex.value
        val list = _weatherLocations.value.toMutableList()
        val current = list[idx]
        val newMod = (current.microClimateModifierTemp + delta).coerceIn(-10f, 10f)
        list[idx] = current.copy(microClimateModifierTemp = newMod)
        _weatherLocations.value = list
    }

    // DEVICE & SMART HOME ACTIONS
    fun toggleFlashlight() {
        val isOn = deviceManager.toggleFlashlight()
        ttsManager.speak(if (isOn) "Illumination active, sir." else "Illumination disengaged.")
    }

    fun adjustVolume(percent: Int) {
        deviceManager.adjustVolume(percent)
    }

    fun adjustBrightness(percent: Int) {
        deviceManager.adjustBrightness(percent)
    }

    fun toggleWifi() {
        deviceManager.toggleWifi()
    }

    fun toggleBluetooth() {
        deviceManager.toggleBluetooth()
    }

    fun toggleCombatDnd() {
        deviceManager.toggleDndCombat()
        val state = deviceTelemetry.value.isDndCombatActive
        ttsManager.speak(if (state) "Combat silence protocol engaged." else "Normal telemetry alerts restored.")
    }

    fun toggleWorkshopLight() {
        val newState = !_smartHomeState.value.isLightOn
        _smartHomeState.value = _smartHomeState.value.copy(isLightOn = newState)
        ttsManager.speak(if (newState) "Workshop illumination online." else "Workshop illumination offline.")
    }

    fun setLightingPreset(preset: LightingPreset) {
        _smartHomeState.value = _smartHomeState.value.copy(lightingPreset = preset, isLightOn = true)
        ttsManager.speak("Workshop lighting adjusted to ${preset.label}.")
    }

    fun setDefenseMode(mode: SecurityDefenseMode) {
        _smartHomeState.value = _smartHomeState.value.copy(defenseMode = mode)
        ttsManager.speak("Stark perimeter security mode set to ${mode.label}, sir.")
    }

    fun toggleVaultLock() {
        val newState = !_smartHomeState.value.isVaultLocked
        _smartHomeState.value = _smartHomeState.value.copy(isVaultLocked = newState)
        ttsManager.speak(if (newState) "Subterranean armor vault hydraulically sealed." else "Armor vault unlocked, sir. Access granted.")
    }

    fun toggleHologramProjector() {
        val newState = !_smartHomeState.value.isHologramActive
        _smartHomeState.value = _smartHomeState.value.copy(isHologramActive = newState)
        ttsManager.speak(if (newState) "3D Holographic projection array active." else "Holographic array switched to standby.")
    }

    fun brewEspresso() {
        viewModelScope.launch {
            _smartHomeState.value = _smartHomeState.value.copy(isBrewingCoffee = true)
            ttsManager.speak("Brewing a double espresso for you immediately, sir.")
            delay(2500)
            _smartHomeState.value = _smartHomeState.value.copy(
                isBrewingCoffee = false,
                coffeeStatus = "ESPRESSO READY // 92°C PERFECT EXTRACTION"
            )
            ttsManager.speak("Your espresso is poured and ready in the workshop, Mr. Stark.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        speechManager.stopListening()
        deviceManager.shutdown()
    }
}
