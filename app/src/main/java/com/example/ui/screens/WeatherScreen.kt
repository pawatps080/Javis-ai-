package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.FlightSafetyStatus
import com.example.data.HourlyForecastItem
import com.example.data.StarkWeatherLocation
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
fun WeatherScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val locations by viewModel.weatherLocations.collectAsState()
    val selectedLocationIndex by viewModel.selectedWeatherLocationIndex.collectAsState()
    val activeLoc = locations.getOrNull(selectedLocationIndex) ?: locations.first()

    val effectiveTemp = activeLoc.tempCelsius + activeLoc.microClimateModifierTemp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("weather_screen")
    ) {
        // Top Atmospheric Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = "Weather Station",
                        tint = JarvisCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ATMOSPHERIC & WEATHER TELEMETRY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 14.sp
                        )
                    )
                }
                Text(
                    text = "GLOBAL METEOROLOGICAL SAT-NET & MICRO-CLIMATE CONTROL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JarvisGoldLight,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Location Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            locations.forEachIndexed { index, loc ->
                val isSelected = index == selectedLocationIndex
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurface)
                        .border(
                            1.dp,
                            if (isSelected) JarvisCyan else JarvisBorderMuted,
                            CutCornerShape(4.dp)
                        )
                        .clickable { viewModel.selectWeatherLocation(index) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("weather_loc_chip_${loc.id.lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (isSelected) JarvisCyanLight else JarvisTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = loc.name.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) JarvisCyanLight else JarvisTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Weather Hero Station Card
        HudCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weather_hero_card"),
            borderColor = when (activeLoc.flightSafety) {
                FlightSafetyStatus.OPTIMAL -> JarvisCyan
                FlightSafetyStatus.CAUTION -> JarvisGold
                FlightSafetyStatus.HAZARD -> JarvisRed
            }
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = activeLoc.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = JarvisTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = activeLoc.subRegion,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = JarvisTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = activeLoc.coordinates,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisCyanLight,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format(Locale.US, "%.1f°C", effectiveTemp),
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = JarvisCyanLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            text = String.format(Locale.US, "Feels like %.1f°C", activeLoc.feelsLikeCelsius + activeLoc.microClimateModifierTemp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Condition description & Flight Safety status
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(activeLoc.flightSafety.clearanceColor).copy(alpha = 0.15f))
                        .border(1.dp, Color(activeLoc.flightSafety.clearanceColor).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = Color(activeLoc.flightSafety.clearanceColor),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeLoc.flightSafety.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(activeLoc.flightSafety.clearanceColor),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Text(
                            text = activeLoc.condition,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = activeLoc.conditionSummary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = JarvisTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Atmospheric Parameter Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherTelemetryBadge("WIND SPEED", String.format(Locale.US, "%.1f km/h %s", activeLoc.windKmh, activeLoc.windDirection))
                    WeatherTelemetryBadge("HUMIDITY", "${activeLoc.humidityPercent}%")
                    WeatherTelemetryBadge("PRESSURE", "${activeLoc.pressureHpa} hPa")
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherTelemetryBadge("UV INDEX", "${activeLoc.uvIndex} / 15")
                    WeatherTelemetryBadge("VISIBILITY", String.format(Locale.US, "%.1f km", activeLoc.visibilityKm))
                    WeatherTelemetryBadge("ION CHARGE", String.format(Locale.US, "%.1f C/m³", activeLoc.ionChargeDensity))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hourly Forecast Ribbon
        Text(
            text = "HOURLY ATMOSPHERIC VECTOR FORECAST",
            style = MaterialTheme.typography.labelSmall.copy(
                color = JarvisGoldLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(activeLoc.hourlyForecast) { item ->
                HourlyForecastCard(item = item)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stark Atmospheric & Weather Control Suite
        Text(
            text = "STARK ATMOSPHERIC & CLIMATE MANIPULATOR",
            style = MaterialTheme.typography.labelSmall.copy(
                color = JarvisCyanLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.height(6.dp))

        HudCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weather_controls_card"),
            borderColor = JarvisCyan
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 1. Precipitation Dissipation Shield Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (activeLoc.isShieldActive) JarvisCyanLight else JarvisTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ION PRECIPITATION DISSIPATOR",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = JarvisTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Text(
                            text = if (activeLoc.isShieldActive) "Storm deflection field active. Rain neutralized." else "Deflection field offline.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (activeLoc.isShieldActive) JarvisCyanLight else JarvisTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Switch(
                        checked = activeLoc.isShieldActive,
                        onCheckedChange = { viewModel.toggleWeatherShield() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JarvisCyanLight,
                            checkedTrackColor = JarvisCyan.copy(alpha = 0.5f),
                            uncheckedThumbColor = JarvisTextMuted,
                            uncheckedTrackColor = JarvisSurface
                        ),
                        modifier = Modifier.testTag("toggle_weather_shield")
                    )
                }

                // 2. Cloud Seeding / Ionization Protocol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Thunderstorm,
                                contentDescription = null,
                                tint = if (activeLoc.isCloudSeedingActive) JarvisGoldLight else JarvisTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OZONE CLOUD SEEDING PROTOCOL",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = JarvisTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Text(
                            text = if (activeLoc.isCloudSeedingActive) "Dispersing ionization silver-iodide flares." else "Atmospheric aerosol emitters standby.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (activeLoc.isCloudSeedingActive) JarvisGoldLight else JarvisTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Switch(
                        checked = activeLoc.isCloudSeedingActive,
                        onCheckedChange = { viewModel.toggleCloudSeeding() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JarvisGoldLight,
                            checkedTrackColor = JarvisGold.copy(alpha = 0.5f),
                            uncheckedThumbColor = JarvisTextMuted,
                            uncheckedTrackColor = JarvisSurface
                        ),
                        modifier = Modifier.testTag("toggle_cloud_seeding")
                    )
                }

                // 3. Micro-Climate Thermal Regulator (+/- Temperature)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JarvisDarkBg.copy(alpha = 0.5f))
                        .border(1.dp, JarvisBorderMuted, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MICRO-CLIMATE THERMAL REGULATOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Offset: ${String.format(Locale.US, "%+.1f°C", activeLoc.microClimateModifierTemp)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = JarvisCyanLight,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(JarvisSurface)
                                    .border(1.dp, JarvisCyan, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.adjustMicroClimateTemp(-1f) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-1°", color = JarvisCyanLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(JarvisSurface)
                                    .border(1.dp, JarvisGold, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.adjustMicroClimateTemp(1f) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+1°", color = JarvisGoldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 4. Request Supersonic Flight Weather Corridor Analysis from Jarvis
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CutCornerShape(4.dp))
                        .background(JarvisCyan.copy(alpha = 0.15f))
                        .border(1.dp, JarvisCyan, CutCornerShape(4.dp))
                        .clickable {
                            viewModel.sendUserMessage("Jarvis, give me an atmospheric supersonic flight vector briefing for ${activeLoc.name} (${activeLoc.coordinates}).")
                            viewModel.setTab(0)
                        }
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .testTag("ask_jarvis_weather_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Flight Corridor",
                            tint = JarvisCyanLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CALCULATE SUPERSONIC FLIGHT CORRIDOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherTelemetryBadge(title: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(JarvisSurface)
            .border(1.dp, JarvisBorderMuted, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisTextMuted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = JarvisTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Composable
fun HourlyForecastCard(item: HourlyForecastItem) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(JarvisSurface)
            .border(1.dp, JarvisBorderMuted, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = item.timeLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisTextMuted,
                    fontSize = 9.sp
                )
            )
            Text(
                text = item.icon,
                fontSize = 18.sp
            )
            Text(
                text = "${item.tempC}°C",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = JarvisCyanLight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )
            Text(
                text = item.condition,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisTextSecondary,
                    fontSize = 8.sp
                )
            )
        }
    }
}
