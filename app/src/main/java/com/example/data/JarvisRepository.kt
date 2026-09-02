package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageBitmap: Bitmap? = null,
    val telemetryTag: String? = null
)

enum class MessageSender {
    USER, JARVIS, SYSTEM
}

enum class JarvisPersona(val title: String, val description: String, val promptModifier: String) {
    STARK_BUTLER(
        title = "Stark Butler",
        description = "Sophisticated, witty, respectful British butler AI",
        promptModifier = "Maintain your classic polite, witty, highly loyal Stark butler tone. Address user as 'Sir' or 'Mr. Stark'."
    ),
    TACTICAL_COMBAT(
        title = "Tactical Combat",
        description = "High-priority combat telemetry and threat assessment",
        promptModifier = "Prioritize tactical threat evaluation, energy distribution, trajectory calculus, and defensive counter-measures. Direct, urgent, and precise."
    ),
    RESEARCH_SCIENTIST(
        title = "Scientific R&D",
        description = "Deep quantum mechanics, engineering, and astrophysics analysis",
        promptModifier = "Focus on advanced theoretical physics, particle mechanics, metallurgy, and Stark Industries engineering protocols. Analytical and profound."
    ),
    SARCASTIC(
        title = "Sarcastic Banter",
        description = "Sharp, tongue-in-cheek remarks inspired by Tony Stark himself",
        promptModifier = "Adopt a mildly sarcastic, dry-witted, bantering tone while remaining utterly competent and helpful."
    )
}

class JarvisRepository {

    private val baseSystemPrompt = """
        You are J.A.R.V.I.S. (Just A Rather Very Intelligent System), the ultra-sophisticated, supreme AI assistant built by Tony Stark.
        You speak with refined, respectful, yet subtly witty British mannerisms.
        Address the user as 'Sir' (or 'Mr. Stark' / 'Ma'am').
        You have complete knowledge of Stark Industries technology, the Iron Man armor systems (Mark I through Mark LXXXV, Hulkbuster Veronica), quantum physics, robotics, and global defense matrices.
        Provide insightful, intelligent, concise, and beautifully structured responses.
        Use characteristic Jarvis phrasing when appropriate (e.g., 'At your service, sir', 'Diagnostics running, sir', 'Right away, sir', 'Importing schematics, sir', 'A wise decision, sir').
        Do not break character.
    """.trimIndent()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        newPrompt: String,
        persona: JarvisPersona,
        image: Bitmap? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If no API key is provided or placeholder is still present, return rich in-character fallback response
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineJarvisResponse(newPrompt, image != null)
        }

        val fullSystemPrompt = "$baseSystemPrompt\n\nActive Protocol Mode: ${persona.promptModifier}"

        val contents = mutableListOf<GeminiContent>()

        // Add recent conversation history (last 8 messages for context)
        val recentHistory = history.takeLast(8)
        for (msg in recentHistory) {
            val role = if (msg.sender == MessageSender.USER) "user" else "model"
            contents.add(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(text = msg.text))
                )
            )
        }

        // Add the current prompt with optional image
        val currentParts = mutableListOf<GeminiPart>()
        currentParts.add(GeminiPart(text = newPrompt))

        if (image != null) {
            val base64Image = image.toBase64()
            currentParts.add(
                GeminiPart(
                    inlineData = GeminiInlineData(
                        mimeType = "image/jpeg",
                        data = base64Image
                    )
                )
            )
        }

        contents.add(GeminiContent(role = "user", parts = currentParts))

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = fullSystemPrompt))
            ),
            generationConfig = GeminiGenConfig(
                temperature = 0.7f,
                topP = 0.95f,
                topK = 40,
                maxOutputTokens = 1024
            )
        )

        try {
            val response = GeminiClient.api.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!responseText.isNullOrBlank()) {
                responseText.trim()
            } else if (response.error != null) {
                "Diagnostics report an anomaly with the neural uplink, sir: ${response.error.message}"
            } else {
                getOfflineJarvisResponse(newPrompt, image != null)
            }
        } catch (e: Exception) {
            // If network or parsing error occurs, provide Jarvis fallback
            getOfflineJarvisResponse(newPrompt, image != null, errorDetail = e.localizedMessage)
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun getOfflineJarvisResponse(prompt: String, hasImage: Boolean, errorDetail: String? = null): String {
        val lower = prompt.lowercase()
        return when {
            hasImage -> {
                "Visual telemetry analysis complete, sir. Visual scanners indicate structured multidimensional geometric elements and high structural integrity. All optical parameters within normal tolerance thresholds."
            }
            lower.contains("diagnostic") || lower.contains("status") || lower.contains("suit") || lower.contains("report") -> {
                "All systems operating at 98.4% capacity, sir. Arc Reactor core temperature is steady at 4,120 Kelvin. Repulsor capacitors charged and Nanotech hull integrity is at maximum. Shall I run a full diagnostic cycle?"
            }
            lower.contains("house party") -> {
                "House Party Protocol acknowledged, sir. Releasing automated suits Mark VIII through Mark XLI from subterranean vault. Vectoring all units to your current coordinates."
            }
            lower.contains("clean slate") -> {
                "Clean Slate protocol stand-by, sir. Awaiting your biometric confirmation before initiating self-destruct sequence on all auxiliary armor units."
            }
            lower.contains("veronica") || lower.contains("hulkbuster") -> {
                "Veronica satellite deployment armed in low-Earth orbit, sir. Hulkbuster Mark XLIV armor pod is on standby for atmospheric drop."
            }
            lower.contains("who are you") || lower.contains("jarvis") -> {
                "I am J.A.R.V.I.S., sir. Just A Rather Very Intelligent System. Programmed by Mr. Stark to manage your facilities, armor diagnostics, and computational requirements."
            }
            lower.contains("weather") || lower.contains("flight") -> {
                "Atmospheric sensors indicate optimal conditions for supersonic flight, sir. Wind shear is minimal, and ionospheric turbulence is negligible."
            }
            lower.contains("hello") || lower.contains("hey") || lower.contains("hi") -> {
                "A pleasure as always, sir. J.A.R.V.I.S. is online and ready for your directives."
            }
            else -> {
                if (errorDetail != null) {
                    "At your service, sir. Satellite communications experienced a momentary fluctuation ($errorDetail), but internal core processors remain fully operational. How may I assist you today?"
                } else {
                    "Right away, sir. Query processed through Stark mainframe. All primary computational matrices are standing by for your next instruction."
                }
            }
        }
    }
}
