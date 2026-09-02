package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LightingPreset
import com.example.data.SecurityDefenseMode
import com.example.ui.JarvisViewModel
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderMuted
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import java.util.Locale

@Composable
fun DeviceAndHomeScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val deviceTelemetry by viewModel.deviceTelemetry.collectAsState()
    val smartHomeState by viewModel.smartHomeState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("device_home_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Smart Home",
                        tint = JarvisCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEVICE TELEMETRY & STARK AUTOMATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 14.sp
                        )
                    )
                }
                Text(
                    text = "REAL HARDWARE CONTROLLER & MANSION IoT MATRIX",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JarvisGoldLight,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 1: Real Android Device & Battery Telemetry
        Text(
            text = "ANDROID DEVICE TELEMETRY & HARDWARE",
            style = MaterialTheme.typography.labelSmall.copy(
                color = JarvisGoldLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(6.dp))

        HudCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("battery_device_card"),
            borderColor = JarvisCyan
        ) {
            Column {
                // Battery Hero Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(JarvisSurface)
                                .border(1.dp, JarvisCyan, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (deviceTelemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                contentDescription = "Battery Status",
                                tint = if (deviceTelemetry.isCharging) JarvisGoldLight else JarvisCyanLight,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${deviceTelemetry.batteryPercent}%",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = JarvisCyanLight,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 22.sp
                                    )
                                )
                                if (deviceTelemetry.isCharging) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(JarvisGold.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "CHARGING",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = JarvisGoldLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "CORE HEALTH: ${deviceTelemetry.batteryHealth} // ${deviceTelemetry.batteryVoltageMv} mV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisTextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format(Locale.US, "%.1f°C", deviceTelemetry.batteryTempCelsius),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = JarvisGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "CELL TEMP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextMuted,
                                fontSize = 8.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hardware Quick Toggle Tiles (Flashlight, Wi-Fi, BT, DND)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Flashlight
                    DeviceToggleTile(
                        icon = Icons.Default.FlashlightOn,
                        title = "FLASHLIGHT",
                        subtitle = if (deviceTelemetry.isFlashlightOn) "ACTIVE" else "OFFLINE",
                        isActive = deviceTelemetry.isFlashlightOn,
                        activeColor = JarvisGoldLight,
                        onClick = { viewModel.toggleFlashlight() },
                        modifier = Modifier.weight(1f),
                        testTag = "toggle_flashlight_btn"
                    )

                    // Wi-Fi
                    DeviceToggleTile(
                        icon = Icons.Default.Wifi,
                        title = "WI-FI LINK",
                        subtitle = if (deviceTelemetry.isWifiActive) "ONLINE" else "OFFLINE",
                        isActive = deviceTelemetry.isWifiActive,
                        activeColor = JarvisCyanLight,
                        onClick = { viewModel.toggleWifi() },
                        modifier = Modifier.weight(1f),
                        testTag = "toggle_wifi_btn"
                    )

                    // Bluetooth
                    DeviceToggleTile(
                        icon = Icons.Default.Bluetooth,
                        title = "BLUETOOTH",
                        subtitle = if (deviceTelemetry.isBluetoothActive) "PAIRED" else "STANDBY",
                        isActive = deviceTelemetry.isBluetoothActive,
                        activeColor = JarvisCyanLight,
                        onClick = { viewModel.toggleBluetooth() },
                        modifier = Modifier.weight(1f),
                        testTag = "toggle_bt_btn"
                    )

                    // Combat DND
                    DeviceToggleTile(
                        icon = Icons.Default.NotificationsOff,
                        title = "COMBAT DND",
                        subtitle = if (deviceTelemetry.isDndCombatActive) "ACTIVE" else "SILENT",
                        isActive = deviceTelemetry.isDndCombatActive,
                        activeColor = JarvisRed,
                        onClick = { viewModel.toggleCombatDnd() },
                        modifier = Modifier.weight(1f),
                        testTag = "toggle_dnd_btn"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Volume Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = JarvisCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUDIO OUTPUT GAIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Text(
                            text = "${deviceTelemetry.volumePercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisCyanLight,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Slider(
                        value = deviceTelemetry.volumePercent.toFloat(),
                        onValueChange = { viewModel.adjustVolume(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = JarvisCyanLight,
                            activeTrackColor = JarvisCyan,
                            inactiveTrackColor = JarvisSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("volume_slider")
                    )
                }

                // Brightness Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BrightnessMedium,
                                contentDescription = null,
                                tint = JarvisGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HUD DISPLAY BRIGHTNESS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Text(
                            text = "${deviceTelemetry.brightnessPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisGoldLight,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Slider(
                        value = deviceTelemetry.brightnessPercent.toFloat(),
                        onValueChange = { viewModel.adjustBrightness(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = JarvisGoldLight,
                            activeTrackColor = JarvisGold,
                            inactiveTrackColor = JarvisSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("brightness_slider")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Stark Mansion Smart Home Automation
        Text(
            text = "STARK MANSION AUTOMATION SUITE",
            style = MaterialTheme.typography.labelSmall.copy(
                color = JarvisCyanLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(6.dp))

        // 1. Workshop Lighting & Hologram Matrix Card
        HudCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("workshop_lighting_card"),
            borderColor = Color(smartHomeState.lightingPreset.colorHex)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(smartHomeState.lightingPreset.colorHex),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "WORKSHOP NEON AMBIENCE",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = JarvisTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "ACTIVE PRESET: ${smartHomeState.lightingPreset.label}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(smartHomeState.lightingPreset.colorHex),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = smartHomeState.isLightOn,
                        onCheckedChange = { viewModel.toggleWorkshopLight() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(smartHomeState.lightingPreset.colorHex),
                            checkedTrackColor = Color(smartHomeState.lightingPreset.colorHex).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("toggle_workshop_light")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Color Preset Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LightingPreset.entries.forEach { preset ->
                        val isSelected = smartHomeState.lightingPreset == preset
                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(4.dp))
                                .background(if (isSelected) Color(preset.colorHex).copy(alpha = 0.3f) else JarvisSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) Color(preset.colorHex) else JarvisBorderMuted,
                                    CutCornerShape(4.dp)
                                )
                                .clickable { viewModel.setLightingPreset(preset) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(preset.colorHex))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = preset.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) JarvisTextPrimary else JarvisTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Perimeter Defense & Sentry Turrets
        HudCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("perimeter_defense_card"),
            borderColor = if (smartHomeState.defenseMode == SecurityDefenseMode.LOCKDOWN) JarvisRed else JarvisGold
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = JarvisGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PERIMETER DEFENSE MATRIX",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = JarvisTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = smartHomeState.defenseMode.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisGoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(JarvisDarkBg)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "0 BREACHES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisCyanLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = smartHomeState.defenseMode.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Security Mode selector buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SecurityDefenseMode.entries.forEach { mode ->
                        val isCurrent = smartHomeState.defenseMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CutCornerShape(4.dp))
                                .background(if (isCurrent) JarvisGold.copy(alpha = 0.25f) else JarvisSurface)
                                .border(
                                    1.dp,
                                    if (isCurrent) JarvisGold else JarvisBorderMuted,
                                    CutCornerShape(4.dp)
                                )
                                .clickable { viewModel.setDefenseMode(mode) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.name.replace("_", " ").take(8),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isCurrent) JarvisGoldLight else JarvisTextSecondary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Vault & Garage Door + Hologram Projector + Coffee Machine Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Vault Door
            HudCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.toggleVaultLock() }
                    .testTag("vault_door_tile"),
                borderColor = if (smartHomeState.isVaultLocked) JarvisCyan else JarvisRed
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (smartHomeState.isVaultLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = if (smartHomeState.isVaultLocked) JarvisCyanLight else JarvisRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ARMOR VAULT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = if (smartHomeState.isVaultLocked) "SEALED" else "UNLOCKED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (smartHomeState.isVaultLocked) JarvisCyanLight else JarvisRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Holographic 3D Projector
            HudCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.toggleHologramProjector() }
                    .testTag("hologram_tile"),
                borderColor = JarvisCyan
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = if (smartHomeState.isHologramActive) JarvisCyanLight else JarvisTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HOLOGRAM 3D",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = if (smartHomeState.isHologramActive) "PROJECTING" else "STANDBY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (smartHomeState.isHologramActive) JarvisCyanLight else JarvisTextMuted,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Espresso Protocol
            HudCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.brewEspresso() }
                    .testTag("espresso_tile"),
                borderColor = JarvisGold
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Coffee,
                        contentDescription = null,
                        tint = if (smartHomeState.isBrewingCoffee) JarvisGoldLight else JarvisGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ESPRESSO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = if (smartHomeState.isBrewingCoffee) "BREWING..." else "BREW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisGoldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceToggleTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .clip(CutCornerShape(6.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.2f) else JarvisSurface)
            .border(
                1.dp,
                if (isActive) activeColor else JarvisBorderMuted,
                CutCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else JarvisTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisTextPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isActive) activeColor else JarvisTextMuted,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
