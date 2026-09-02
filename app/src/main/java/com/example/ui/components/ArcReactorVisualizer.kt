package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDark
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorVisualizer(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    isListening: Boolean = false,
    isSpeaking: Boolean = false,
    isThinking: Boolean = false,
    audioLevel: Float = 0f,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcReactorInfinite")

    // Slow continuous rotations
    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThinking || isListening) 6000 else 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rotation"
    )

    val rotationInner by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThinking) 4000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rotation"
    )

    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 400 else if (isThinking) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 500 else 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .testTag("arc_reactor_visualizer")
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = size / 2),
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxRadius = (this.size.minDimension / 2f) * 0.95f
            
            // Dynamic boost factor based on audio/speaking
            val audioBoost = if (isSpeaking || isListening) (audioLevel * 0.4f) + 0.1f else 0f
            val dynamicCoreScale = (corePulse + audioBoost).coerceIn(0.8f, 1.5f)

            // 1. Outer Hologram Glow Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JarvisCyan.copy(alpha = 0.25f * glowPulse),
                        JarvisCyanGlow.copy(alpha = 0.1f * glowPulse),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.05f
                ),
                radius = maxRadius * 1.05f,
                center = center
            )

            // 2. Outermost Segmented Telemetry Ring
            rotate(rotationOuter, pivot = center) {
                drawOuterTelemetryRing(center, maxRadius)
            }

            // 3. Middle Segmented Stator Blades (Iron Man Arc Reactor Tri-sections & segments)
            rotate(rotationInner, pivot = center) {
                drawArcSegments(center, maxRadius * 0.78f, isListening || isSpeaking)
            }

            // 4. Equalizer / Waveform Energy Spikes (Reacting to audio & voice)
            drawEqualizerSpikes(
                center = center,
                radius = maxRadius * 0.62f,
                isSpeaking = isSpeaking,
                isListening = isListening,
                audioLevel = audioLevel,
                rotation = rotationOuter
            )

            // 5. Inner Core Ring & Tech Ticks
            rotate(rotationOuter * 1.5f, pivot = center) {
                drawInnerTicks(center, maxRadius * 0.45f)
            }

            // 6. Central Arc Reactor Core Bulb (Vibrant glowing center with concentric rings)
            drawCoreCenter(
                center = center,
                coreRadius = maxRadius * 0.32f * dynamicCoreScale,
                isListening = isListening,
                isSpeaking = isSpeaking,
                isThinking = isThinking
            )
        }
    }
}

private fun DrawScope.drawOuterTelemetryRing(center: Offset, radius: Float) {
    // Thin outer track
    drawCircle(
        color = JarvisCyan.copy(alpha = 0.35f),
        radius = radius,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Segmented dashed track
    drawCircle(
        color = JarvisCyanLight.copy(alpha = 0.8f),
        radius = radius * 0.94f,
        center = center,
        style = Stroke(
            width = 3.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f, 10f, 15f), 0f)
        )
    )

    // Four Cardinal Tech Corner Brackets
    val bracketRadius = radius * 0.98f
    for (angle in listOf(0f, 90f, 180f, 270f)) {
        val rad = angle * (PI / 180f)
        val x = center.x + bracketRadius * cos(rad).toFloat()
        val y = center.y + bracketRadius * sin(rad).toFloat()
        drawCircle(
            color = JarvisGold,
            radius = 3.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawArcSegments(center: Offset, radius: Float, isHighEnergy: Boolean) {
    val segmentCount = 10
    val sweepAngle = 24f
    val gapAngle = 12f
    val strokeWidth = 8.dp.toPx()

    for (i in 0 until segmentCount) {
        val startAngle = i * (sweepAngle + gapAngle)
        val color = if (i % 3 == 0) JarvisGoldLight else JarvisCyan

        drawArc(
            color = color.copy(alpha = if (isHighEnergy) 0.95f else 0.75f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }

    // Inner bounding thin ring
    drawCircle(
        color = JarvisCyan.copy(alpha = 0.5f),
        radius = radius - strokeWidth,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawEqualizerSpikes(
    center: Offset,
    radius: Float,
    isSpeaking: Boolean,
    isListening: Boolean,
    audioLevel: Float,
    rotation: Float
) {
    val spikeCount = 24
    for (i in 0 until spikeCount) {
        val baseAngle = (i * (360f / spikeCount) + rotation) * (PI / 180f)
        
        // Compute dynamic height of spike
        val dynamicMultiplier = when {
            isSpeaking -> (sin(i * 1.5f + rotation * 0.1f) * 0.5f + 0.5f) * 0.8f + 0.2f
            isListening -> (audioLevel * 1.2f + (sin(i * 2f) * 0.2f)).coerceIn(0.1f, 1f)
            else -> 0.15f + (sin(i * 0.8f) * 0.08f)
        }
        
        val spikeLength = 12.dp.toPx() * (1f + dynamicMultiplier * 2.5f)
        val innerR = radius - (spikeLength / 2f)
        val outerR = radius + (spikeLength / 2f)

        val startX = center.x + innerR * cos(baseAngle).toFloat()
        val startY = center.y + innerR * sin(baseAngle).toFloat()
        val endX = center.x + outerR * cos(baseAngle).toFloat()
        val endY = center.y + outerR * sin(baseAngle).toFloat()

        val spikeColor = if (isListening) JarvisGold else JarvisCyanLight

        drawLine(
            color = spikeColor.copy(alpha = (0.4f + dynamicMultiplier * 0.6f).coerceIn(0.2f, 1f)),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawInnerTicks(center: Offset, radius: Float) {
    drawCircle(
        color = JarvisCyan.copy(alpha = 0.4f),
        radius = radius,
        center = center,
        style = Stroke(
            width = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 12f), 0f)
        )
    )

    // Inner triangle / triangular core anchor
    val triRadius = radius * 0.85f
    val points = listOf(
        Offset(center.x, center.y - triRadius),
        Offset(center.x + triRadius * 0.866f, center.y + triRadius * 0.5f),
        Offset(center.x - triRadius * 0.866f, center.y + triRadius * 0.5f)
    )
    for (i in points.indices) {
        val next = points[(i + 1) % points.size]
        drawLine(
            color = JarvisCyanDark.copy(alpha = 0.6f),
            start = points[i],
            end = next,
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

private fun DrawScope.drawCoreCenter(
    center: Offset,
    coreRadius: Float,
    isListening: Boolean,
    isSpeaking: Boolean,
    isThinking: Boolean
) {
    val coreColor = when {
        isListening -> JarvisGold
        isThinking -> JarvisGoldLight
        else -> JarvisCyan
    }

    // Radial gradient core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                coreColor,
                coreColor.copy(alpha = 0.4f),
                Color.Transparent
            ),
            center = center,
            radius = coreRadius
        ),
        radius = coreRadius,
        center = center
    )

    // Central bright dot
    drawCircle(
        color = Color.White,
        radius = coreRadius * 0.35f,
        center = center
    )

    // Concentric core ring
    drawCircle(
        color = JarvisCyanLight,
        radius = coreRadius * 0.7f,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )
}
