package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JarvisPersona
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderMuted
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDark
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisPanelBgSemi
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import kotlinx.coroutines.delay

/**
 * Holographic HUD Card with futuristic corner cutouts and border accents
 */
@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    borderColor: Color = JarvisBorder,
    backgroundColor: Color = JarvisPanelBgSemi,
    cornerCut: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CutCornerShape(topStart = cornerCut, bottomEnd = cornerCut))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, borderColor.copy(alpha = 0.3f), borderColor)
                ),
                shape = CutCornerShape(topStart = cornerCut, bottomEnd = cornerCut)
            )
    ) {
        // Tech Corner Brackets Canvas
        Canvas(modifier = Modifier.matchParentSize()) {
            val bracketLen = 14.dp.toPx()
            val strokeW = 2.dp.toPx()

            // Top-left bracket
            val pathTL = Path().apply {
                moveTo(0f, bracketLen)
                lineTo(0f, 0f)
                lineTo(bracketLen, 0f)
            }
            drawPath(pathTL, color = JarvisCyanLight, style = Stroke(width = strokeW))

            // Bottom-right bracket
            val pathBR = Path().apply {
                moveTo(size.width, size.height - bracketLen)
                lineTo(size.width, size.height)
                lineTo(size.width - bracketLen, size.height)
            }
            drawPath(pathBR, color = JarvisCyanLight, style = Stroke(width = strokeW))
        }

        Box(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}

/**
 * Top Stark Industries HUD Header
 */
@Composable
fun HudHeader(
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    activePersona: JarvisPersona,
    onOpenPersonaDialog: () -> Unit,
    onResetProtocols: () -> Unit,
    batteryPercent: Int = 88,
    isCharging: Boolean = false,
    onBatteryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HeaderGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(JarvisDarkBg, JarvisPanelBgSemi.copy(alpha = 0.8f))
                )
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stark Logo & Jarvis ID
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(JarvisSurface)
                        .border(1.dp, JarvisCyan.copy(alpha = glowAlpha), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Arc Reactor Icon",
                        tint = JarvisCyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "J.A.R.V.I.S.",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = JarvisCyan,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(JarvisCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "MK-LXXXV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JarvisCyanLight,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Text(
                        text = "STARK INDUSTRIES AI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisGoldLight,
                            letterSpacing = 1.sp,
                            fontSize = 8.sp
                        )
                    )
                }
            }

            // Quick Control Action Icons + Battery Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Real Battery Indicator Pill
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(JarvisSurface)
                        .border(1.dp, if (isCharging) JarvisGold else JarvisCyan.copy(alpha = 0.6f), CutCornerShape(4.dp))
                        .clickable(onClick = onBatteryClick)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("header_battery_indicator")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isCharging -> Icons.Default.BatteryChargingFull
                                batteryPercent <= 15 -> Icons.Default.BatteryAlert
                                else -> Icons.Default.BatteryFull
                            },
                            contentDescription = "Battery Status",
                            tint = when {
                                isCharging -> JarvisGoldLight
                                batteryPercent <= 15 -> JarvisRed
                                else -> JarvisCyanLight
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$batteryPercent%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isCharging) JarvisGoldLight else JarvisTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Persona button
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(JarvisSurface)
                        .border(1.dp, JarvisBorderMuted, CutCornerShape(4.dp))
                        .clickable(onClick = onOpenPersonaDialog)
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                        .testTag("persona_selector_btn")
                ) {
                    Text(
                        text = activePersona.title.take(6).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Mute / Unmute Button
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("audio_mute_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "Unmute Jarvis Voice" else "Mute Jarvis Voice",
                        tint = if (isMuted) JarvisTextMuted else JarvisCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Reset / Reconnect Button
                IconButton(
                    onClick = onResetProtocols,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("reset_protocols_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Restart Protocol",
                        tint = JarvisGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Futuristic Horizontal Telemetry Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(JarvisCyan)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "NEURAL LINK: OPTIMAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JarvisCyanLight,
                        fontSize = 8.sp
                    )
                )
            }

            Text(
                text = "SAT_FEED: BTC $89.4K // S&P 5.8K",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisGoldLight,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text = "ARC: 98.4%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisCyanLight,
                    fontSize = 8.sp
                )
            )
        }
    }
}

/**
 * High-tech Sci-Fi Tab Bar with All Modules
 */
@Composable
fun HudNavigationRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "CORE",
        "MARKETS",
        "WEATHER",
        "DEVICES",
        "DIAGNOSTICS",
        "SCANNER",
        "PROTOCOLS"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(JarvisPanelBgSemi)
            .border(
                BorderStroke(1.dp, JarvisBorderMuted),
                RoundedCornerShape(0.dp)
            )
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .clip(CutCornerShape(5.dp))
                    .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurface)
                    .border(
                        width = if (isSelected) 1.dp else 1.dp,
                        color = if (isSelected) JarvisCyan else JarvisBorderMuted,
                        shape = CutCornerShape(5.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { onTabSelected(index) }
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp)
                    .testTag("nav_tab_$index"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isSelected) JarvisCyanLight else JarvisTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Metric Telemetry Pill
 */
@Composable
fun TelemetryPill(
    label: String,
    value: String,
    color: Color = JarvisCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CutCornerShape(4.dp))
            .background(JarvisSurface)
            .border(1.dp, color.copy(alpha = 0.4f), CutCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisTextMuted,
                    fontSize = 9.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
        }
    }
}

/**
 * Typewriter effect for Jarvis responses
 */
@Composable
fun TypingTerminalText(
    text: String,
    modifier: Modifier = Modifier,
    typingSpeedMs: Long = 12L,
    onComplete: (() -> Unit)? = null
) {
    var displayedLength by remember(text) { mutableIntStateOf(0) }

    LaunchedEffect(text) {
        displayedLength = 0
        while (displayedLength < text.length) {
            // Speed up if long text
            val step = if (text.length > 300) 4 else 2
            displayedLength = (displayedLength + step).coerceAtMost(text.length)
            delay(typingSpeedMs)
        }
        onComplete?.invoke()
    }

    Text(
        text = text.take(displayedLength),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Default,
            lineHeight = 22.sp,
            color = JarvisTextPrimary
        ),
        modifier = modifier
    )
}
