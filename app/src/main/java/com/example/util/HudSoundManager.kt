package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class HudSoundType {
    CORE_MODE,
    MARKETS_MODE,
    WEATHER_MODE,
    DEVICES_MODE,
    DIAGNOSTICS_MODE,
    SCANNER_MODE,
    PROTOCOLS_MODE,
    BUTTON_TAP,
    TACTICAL_PING,
    ALERT_CHIME
}

/**
 * Procedural Sci-Fi HUD Audio Synthesizer for Iron Man Mark LXXXV interface.
 * Generates low-latency, crisp PCM acoustic and electronic telemetry feedback cues
 * without needing external large asset files.
 */
class HudSoundManager(private val context: Context) {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    // Precomputed sound sample buffers
    private val soundBuffers = mutableMapOf<HudSoundType, ShortArray>()
    private var isEnabled = true

    init {
        scope.launch {
            precomputeAllSounds()
        }
    }

    private fun precomputeAllSounds() {
        // Tab 0: Core HUD - Clean futuristic dual-frequency pulse (880Hz + 1760Hz shimmer)
        soundBuffers[HudSoundType.CORE_MODE] = generateSciFiTone(
            durationMs = 90,
            freqStart = 1100.0,
            freqEnd = 1650.0,
            harmonics = listOf(1.0 to 0.6, 2.0 to 0.25, 0.5 to 0.15),
            decayRate = 30.0
        )

        // Tab 1: Markets & Crypto - Digital data blip arpeggio
        soundBuffers[HudSoundType.MARKETS_MODE] = generateArpeggioTone(
            frequencies = doubleArrayOf(980.0, 1310.0, 1960.0),
            noteDurationMs = 28,
            decayRate = 35.0
        )

        // Tab 2: Weather Telemetry - Atmospheric resonant downward frequency sweep
        soundBuffers[HudSoundType.WEATHER_MODE] = generateSciFiTone(
            durationMs = 110,
            freqStart = 1450.0,
            freqEnd = 850.0,
            harmonics = listOf(1.0 to 0.7, 1.5 to 0.3),
            decayRate = 24.0
        )

        // Tab 3: Devices & Hardware - Crisp electrical servo click / capacitor energize
        soundBuffers[HudSoundType.DEVICES_MODE] = generateDualPulse(
            freq1 = 720.0,
            freq2 = 1440.0,
            gapMs = 18,
            pulseDurationMs = 35
        )

        // Tab 4: Diagnostics Sweep - Multi-harmonic diagnostic crystal chime
        soundBuffers[HudSoundType.DIAGNOSTICS_MODE] = generateArpeggioTone(
            frequencies = doubleArrayOf(1046.5, 1318.5, 1567.98, 2093.0),
            noteDurationMs = 25,
            decayRate = 28.0
        )

        // Tab 5: Tactical Scanner - Damped Sonar / Radar high-Q ping
        soundBuffers[HudSoundType.SCANNER_MODE] = generateSciFiTone(
            durationMs = 120,
            freqStart = 1750.0,
            freqEnd = 1550.0,
            harmonics = listOf(1.0 to 0.8, 3.0 to 0.15),
            decayRate = 20.0
        )

        // Tab 6: Protocol Archives - Heavy tactical authorization double-warble
        soundBuffers[HudSoundType.PROTOCOLS_MODE] = generateDualPulse(
            freq1 = 800.0,
            freq2 = 1200.0,
            gapMs = 22,
            pulseDurationMs = 45
        )

        // General button click - 18ms subtle tactile micro-click
        soundBuffers[HudSoundType.BUTTON_TAP] = generateSciFiTone(
            durationMs = 22,
            freqStart = 2200.0,
            freqEnd = 1100.0,
            harmonics = listOf(1.0 to 0.8),
            decayRate = 90.0
        )

        // Tactical Ping
        soundBuffers[HudSoundType.TACTICAL_PING] = generateSciFiTone(
            durationMs = 80,
            freqStart = 2100.0,
            freqEnd = 1800.0,
            harmonics = listOf(1.0 to 0.75, 2.0 to 0.25),
            decayRate = 35.0
        )

        // Alert Chime
        soundBuffers[HudSoundType.ALERT_CHIME] = generateArpeggioTone(
            frequencies = doubleArrayOf(1500.0, 1100.0),
            noteDurationMs = 50,
            decayRate = 25.0
        )
    }

    fun setSoundEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun isSoundEnabled(): Boolean = isEnabled

    fun playTabSwitch(tabIndex: Int) {
        if (!isEnabled) return
        val soundType = when (tabIndex) {
            0 -> HudSoundType.CORE_MODE
            1 -> HudSoundType.MARKETS_MODE
            2 -> HudSoundType.WEATHER_MODE
            3 -> HudSoundType.DEVICES_MODE
            4 -> HudSoundType.DIAGNOSTICS_MODE
            5 -> HudSoundType.SCANNER_MODE
            6 -> HudSoundType.PROTOCOLS_MODE
            else -> HudSoundType.CORE_MODE
        }
        playSound(soundType)
    }

    fun playSound(type: HudSoundType) {
        if (!isEnabled) return
        scope.launch {
            val buffer = soundBuffers[type] ?: return@launch
            playPcmBuffer(buffer)
        }
    }

    private suspend fun playPcmBuffer(buffer: ShortArray) {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(buffer.size * 2, minBufSize)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            // Release after track finishes playing
            val trackDurationMs = ((buffer.size.toDouble() / sampleRate) * 1000).toLong() + 50
            delay(trackDurationMs)
            track.stop()
            track.release()
        } catch (_: Exception) {
            // Graceful fallback if audio device is unavailable
        }
    }

    // Mathematical Waveform Generators with Smooth Anti-Pop Envelopes
    private fun generateSciFiTone(
        durationMs: Int,
        freqStart: Double,
        freqEnd: Double,
        harmonics: List<Pair<Double, Double>>,
        decayRate: Double
    ): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        var phase = 0.0

        val attackSamples = (sampleRate * 0.004).toInt().coerceAtLeast(1) // 4ms attack

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples
            val currentFreq = freqStart + (freqEnd - freqStart) * progress

            phase += 2.0 * PI * currentFreq / sampleRate

            var sampleValue = 0.0
            harmonics.forEach { (mult, weight) ->
                sampleValue += weight * sin(phase * mult)
            }

            // Envelope: fast linear attack, exponential smooth sci-fi decay
            val attackEnv = if (i < attackSamples) i.toDouble() / attackSamples else 1.0
            val decayEnv = exp(-decayRate * t)
            val envelope = attackEnv * decayEnv

            val finalSample = (sampleValue * envelope * 24000.0).coerceIn(-32767.0, 32767.0)
            buffer[i] = finalSample.toInt().toShort()
        }
        return buffer
    }

    private fun generateArpeggioTone(
        frequencies: DoubleArray,
        noteDurationMs: Int,
        decayRate: Double
    ): ShortArray {
        val noteSamples = (sampleRate * (noteDurationMs / 1000.0)).toInt()
        val totalSamples = noteSamples * frequencies.size
        val buffer = ShortArray(totalSamples)

        frequencies.forEachIndexed { noteIdx, freq ->
            var phase = 0.0
            val attackSamples = (sampleRate * 0.003).toInt().coerceAtLeast(1)

            for (i in 0 until noteSamples) {
                val t = i.toDouble() / sampleRate
                phase += 2.0 * PI * freq / sampleRate

                val raw = sin(phase) + 0.3 * sin(phase * 2.0)
                val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0
                val decay = exp(-decayRate * t)
                val env = attack * decay

                val globalIdx = noteIdx * noteSamples + i
                if (globalIdx < totalSamples) {
                    val sample = (raw * env * 22000.0).coerceIn(-32767.0, 32767.0)
                    buffer[globalIdx] = sample.toInt().toShort()
                }
            }
        }
        return buffer
    }

    private fun generateDualPulse(
        freq1: Double,
        freq2: Double,
        gapMs: Int,
        pulseDurationMs: Int
    ): ShortArray {
        val pulse1 = generateSciFiTone(pulseDurationMs, freq1, freq1 * 1.15, listOf(1.0 to 0.7, 2.0 to 0.3), 35.0)
        val gapSamples = (sampleRate * (gapMs / 1000.0)).toInt()
        val pulse2 = generateSciFiTone(pulseDurationMs, freq2, freq2 * 1.15, listOf(1.0 to 0.7, 2.0 to 0.3), 35.0)

        val totalSamples = pulse1.size + gapSamples + pulse2.size
        val buffer = ShortArray(totalSamples)

        System.arraycopy(pulse1, 0, buffer, 0, pulse1.size)
        // gap remains zero
        System.arraycopy(pulse2, 0, buffer, pulse1.size + gapSamples, pulse2.size)

        return buffer
    }

    fun shutdown() {
        soundBuffers.clear()
    }
}
