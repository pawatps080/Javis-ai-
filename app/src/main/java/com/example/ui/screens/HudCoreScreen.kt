package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ChatMessage
import com.example.data.MessageSender
import com.example.ui.JarvisViewModel
import com.example.ui.components.ArcReactorVisualizer
import com.example.ui.components.HudCard
import com.example.ui.components.TypingTerminalText
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderMuted
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDark
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import com.example.ui.theme.JarvisPanelBg
import com.example.ui.theme.JarvisPanelBgSemi
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBright
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun HudCoreScreen(
    viewModel: JarvisViewModel,
    onNavigateToScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val audioLevel by viewModel.audioLevel.collectAsState()
    val recognizedText by viewModel.recognizedText.collectAsState()
    val speechError by viewModel.speechError.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Permission launcher for voice recording
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleVoiceListening()
        }
    }

    fun handleMicClick() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.toggleVoiceListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .imePadding()
    ) {
        // Upper Holographic Section: Central Arc Reactor & Telemetry Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(JarvisPanelBg, JarvisDarkBg)
                    )
                )
                .padding(top = 8.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Main Hologram Arc Reactor
                ArcReactorVisualizer(
                    size = 180.dp,
                    isListening = isListening,
                    isSpeaking = isSpeaking,
                    isThinking = isThinking,
                    audioLevel = audioLevel,
                    onClick = { handleMicClick() }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Reactive Status Text with Neon Glow
                val statusText = when {
                    isListening -> "LISTENING TO VOICE COMMAND..."
                    isSpeaking -> "J.A.R.V.I.S. VOCALIZING..."
                    isThinking -> "PROCESSING WITH STARK MAINFRAME..."
                    else -> "TAP REACTOR OR MIC TO SPEAK"
                }
                val statusColor = when {
                    isListening -> JarvisGold
                    isSpeaking -> JarvisCyanLight
                    isThinking -> JarvisGoldLight
                    else -> JarvisTextMuted
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = statusColor,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )

                // Voice Live Transcription indicator
                if (isListening && recognizedText.isNotBlank()) {
                    Text(
                        text = "\"$recognizedText\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = JarvisGoldLight,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                if (speechError != null) {
                    Text(
                        text = speechError.orEmpty(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisGoldLight,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Quick Tactical Prompt Chips Row
        val quickPrompts = listOf(
            "Bitcoin & Market Index",
            "Atmospheric weather status",
            "Mansion smart home report",
            "Suit status report",
            "Run diagnostic check",
            "Deploy Veronica"
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(JarvisSurface)
                        .border(1.dp, JarvisBorderMuted, CutCornerShape(4.dp))
                        .clickable { viewModel.sendUserMessage(prompt) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = prompt.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisCyanLight,
                            letterSpacing = 0.8.sp,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Holographic Dialogue Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(message = message)
            }

            if (isThinking) {
                item {
                    ThinkingIndicator()
                }
            }
        }

        // Input Console Bar
        Surface(
            color = JarvisPanelBgSemi,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tactical Scanner Camera Button
                IconButton(
                    onClick = onNavigateToScanner,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CutCornerShape(6.dp))
                        .background(JarvisSurface)
                        .border(1.dp, JarvisBorderMuted, CutCornerShape(6.dp))
                        .testTag("scanner_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Tactical Scanner",
                        tint = JarvisCyan
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Input Field
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = "Command J.A.R.V.I.S...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = JarvisTextMuted,
                                fontSize = 13.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hud_text_input"),
                    shape = CutCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = JarvisBorderMuted,
                        focusedTextColor = JarvisTextPrimary,
                        unfocusedTextColor = JarvisTextPrimary,
                        focusedContainerColor = JarvisSurface,
                        unfocusedContainerColor = JarvisSurface
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = JarvisTextPrimary,
                        fontSize = 13.sp
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Microphone or Send Button
                if (textInput.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val text = textInput.trim()
                            textInput = ""
                            viewModel.sendUserMessage(text)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CutCornerShape(6.dp))
                            .background(JarvisCyan)
                            .testTag("hud_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Command",
                            tint = JarvisDarkBg
                        )
                    }
                } else {
                    IconButton(
                        onClick = { handleMicClick() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isListening) JarvisGold else JarvisCyan)
                            .border(
                                2.dp,
                                if (isListening) JarvisGoldLight else JarvisCyanLight,
                                CircleShape
                            )
                            .testTag("hud_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Voice Input",
                            tint = JarvisDarkBg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isJarvis = message.sender == MessageSender.JARVIS
    val isSystem = message.sender == MessageSender.SYSTEM

    val alignment = if (isJarvis || isSystem) Alignment.Start else Alignment.End
    val borderColor = when {
        isSystem -> JarvisGold
        isJarvis -> JarvisCyan
        else -> JarvisBorderMuted
    }
    val headerTitle = when {
        isSystem -> "SYS // DIRECTIVE"
        isJarvis -> "J.A.R.V.I.S. // AI RESPONSE"
        else -> "MR. STARK // COMMAND"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        HudCard(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("chat_card_${message.id}"),
            borderColor = borderColor,
            backgroundColor = if (isJarvis) JarvisPanelBgSemi else JarvisSurface
        ) {
            Column {
                // Card Header Row with Telemetry Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isJarvis) JarvisCyanLight else JarvisGoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp
                        )
                    )

                    if (message.telemetryTag != null) {
                        Text(
                            text = "[${message.telemetryTag}]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextMuted,
                                fontSize = 8.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Message Body
                if (isJarvis) {
                    TypingTerminalText(text = message.text)
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = JarvisTextPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ThinkingIndicator() {
    HudCard(
        modifier = Modifier.fillMaxWidth(0.7f),
        borderColor = JarvisGoldLight
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = JarvisGoldLight,
                strokeWidth = 2.dp
            )
            Text(
                text = "ACCESSING STARK NEURAL CORES...",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JarvisGoldLight,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )
        }
    }
}
