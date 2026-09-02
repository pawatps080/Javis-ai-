package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderMuted
import com.example.ui.theme.JarvisCrimson
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDark
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import com.example.ui.theme.JarvisPanelBg
import com.example.ui.theme.JarvisPanelBgSemi
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun DiagnosticsScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val diagnostics by viewModel.diagnostics.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: Armor Status Overview
        item {
            HudCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("armor_overview_card"),
                borderColor = JarvisCyan
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MARK LXXXV // NANOTECH",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JarvisCyan
                                )
                            )
                            Text(
                                text = "STARK MAIN TELEMETRY HUD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisGoldLight,
                                    fontSize = 9.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(4.dp))
                                .background(JarvisCyan.copy(alpha = 0.2f))
                                .border(1.dp, JarvisCyan, CutCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ONLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisCyanLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Arc Reactor Output Gauge
                    DiagnosticProgressBar(
                        icon = Icons.Default.BatteryChargingFull,
                        title = "ARC REACTOR CORE POWER",
                        value = "${String.format("%.1f", diagnostics.arcReactorOutput)}%",
                        progress = diagnostics.arcReactorOutput / 100f,
                        color = JarvisCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Nanotech Armor Hull Integrity
                    DiagnosticProgressBar(
                        icon = Icons.Default.Security,
                        title = "NANOTECH HULL INTEGRITY",
                        value = "${String.format("%.1f", diagnostics.nanotechIntegrity)}%",
                        progress = diagnostics.nanotechIntegrity / 100f,
                        color = JarvisGold
                    )
                }
            }
        }

        // Section 2: Sub-systems Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Core Temperature Tile
                HudCard(
                    modifier = Modifier.weight(1f),
                    borderColor = JarvisGold
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = JarvisGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CORE TEMP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisGoldLight,
                                    fontSize = 9.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${diagnostics.coreTempKelvin} K",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = JarvisTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "OPTIMAL THERMAL FLUX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextMuted,
                                fontSize = 8.sp
                            )
                        )
                    }
                }

                // Thrusters Velocity Tile
                HudCard(
                    modifier = Modifier.weight(1f),
                    borderColor = JarvisCyan
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = JarvisCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "THRUST EFFICIENCY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisCyanLight,
                                    fontSize = 9.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${diagnostics.thrusterEfficiency}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = JarvisTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "ION THRUSTER READY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextMuted,
                                fontSize = 8.sp
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Active Defense Matrix
        item {
            HudCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = JarvisBorderMuted
            ) {
                Column {
                    Text(
                        text = "ACTIVE PROTOCOLS & NETWORK UPLINKS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    diagnostics.activeProtocols.forEach { protocol ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = JarvisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = protocol,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisTextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "ENGAGED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisCyanDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Action Button to run diagnostic cycle
        item {
            Button(
                onClick = { viewModel.runDiagnosticCycle() },
                enabled = !isThinking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(CutCornerShape(8.dp))
                    .testTag("run_diagnostic_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JarvisCyan,
                    contentColor = JarvisDarkBg
                ),
                shape = CutCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Run Diagnostics",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isThinking) "DIAGNOSTIC SEQUENCE RUNNING..." else "INITIATE FULL DIAGNOSTIC CYCLE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DiagnosticProgressBar(
    icon: ImageVector,
    title: String,
    value: String,
    progress: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "diag_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JarvisTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = JarvisSurface
        )
    }
}
