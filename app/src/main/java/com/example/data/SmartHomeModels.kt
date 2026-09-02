package com.example.data

enum class SecurityDefenseMode(val label: String, val description: String) {
    ACTIVE_DEFENSE("ARMED DEFENSE", "Automated perimeter micro-repulsor turrets active"),
    PATROL_SWEEP("TACTICAL PATROL", "Infrared drone sweep & biometric monitoring"),
    STANDBY("STANDBY MONITOR", "Low-power radar & motion detection"),
    LOCKDOWN("CODE BLACK LOCKDOWN", "Blast shutters sealed, localized forcefield up")
}

enum class LightingPreset(val label: String, val colorHex: Long) {
    ARC_CYAN("ARC BLUE", 0xFF00E5FF),
    CRIMSON_MARK("MARK LXXXV RED", 0xFFFF3B30),
    STARK_GOLD("STARK GOLD", 0xFFFFD700),
    STEALTH_DIM("STEALTH PURPLE", 0xFF9D4EDD),
    WHITE_LAB("100% CLEAN ROOM", 0xFFFFFFFF)
}

data class SmartHomeState(
    // Workshop Lighting
    val isLightOn: Boolean = true,
    val lightingBrightness: Float = 0.85f,
    val lightingPreset: LightingPreset = LightingPreset.ARC_CYAN,

    // Security & Defense
    val defenseMode: SecurityDefenseMode = SecurityDefenseMode.PATROL_SWEEP,
    val perimeterBreaches: Int = 0,

    // Climate HVAC
    val hvacActive: Boolean = true,
    val targetIndoorTempC: Float = 21.5f,
    val airFiltrationQualityPercent: Int = 99,

    // Armor Vault & Garage
    val isVaultLocked: Boolean = true,
    val isGarageDoorOpen: Boolean = false,

    // Holographic Display Matrix
    val isHologramActive: Boolean = true,
    val hologramModel: String = "MARK_LXXXV_CHASSIS_3D",

    // Coffee / Espresso
    val isBrewingCoffee: Boolean = false,
    val coffeeStatus: String = "STANDBY // STARK ROAST READY",

    // Power Grid
    val mansionArcPowerKw: Float = 1450.8f,
    val isPowerGridFeedbackOn: Boolean = true
)

data class RealDeviceTelemetry(
    val batteryPercent: Int = 88,
    val isCharging: Boolean = false,
    val isPowerSaveMode: Boolean = false,
    val batteryHealth: String = "GOOD",
    val batteryVoltageMv: Int = 4150,
    val batteryTempCelsius: Float = 31.2f,
    val isFlashlightOn: Boolean = false,
    val volumePercent: Int = 75,
    val brightnessPercent: Int = 80,
    val isWifiActive: Boolean = true,
    val isBluetoothActive: Boolean = true,
    val isDndCombatActive: Boolean = false
)
