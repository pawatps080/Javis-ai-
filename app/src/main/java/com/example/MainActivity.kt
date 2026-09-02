package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.JarvisPersona
import com.example.ui.JarvisViewModel
import com.example.ui.components.HudCard
import com.example.ui.components.HudHeader
import com.example.ui.components.HudNavigationRow
import com.example.ui.screens.DeviceAndHomeScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.HudCoreScreen
import com.example.ui.screens.MarketsScreen
import com.example.ui.screens.ProtocolArchiveScreen
import com.example.ui.screens.TacticalScannerScreen
import com.example.ui.screens.WeatherScreen
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderMuted
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import com.example.ui.theme.JarvisPanelBg
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                JarvisApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun JarvisApp(
    viewModel: JarvisViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val activePersona by viewModel.persona.collectAsState()
    val deviceTelemetry by viewModel.deviceTelemetry.collectAsState()

    var showPersonaDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            HudHeader(
                isMuted = isMuted,
                onToggleMute = { viewModel.toggleMute() },
                activePersona = activePersona,
                onOpenPersonaDialog = { showPersonaDialog = true },
                onResetProtocols = { viewModel.clearHistory() },
                batteryPercent = deviceTelemetry.batteryPercent,
                isCharging = deviceTelemetry.isCharging,
                onBatteryClick = { viewModel.setTab(3) }
            )
        },
        bottomBar = {
            HudNavigationRow(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(JarvisDarkBg)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "hud_tab_switch"
            ) { targetTab ->
                when (targetTab) {
                    0 -> HudCoreScreen(
                        viewModel = viewModel,
                        onNavigateToScanner = { viewModel.setTab(5) }
                    )
                    1 -> MarketsScreen(viewModel = viewModel)
                    2 -> WeatherScreen(viewModel = viewModel)
                    3 -> DeviceAndHomeScreen(viewModel = viewModel)
                    4 -> DiagnosticsScreen(viewModel = viewModel)
                    5 -> TacticalScannerScreen(
                        viewModel = viewModel,
                        onNavigateToCore = { viewModel.setTab(0) }
                    )
                    6 -> ProtocolArchiveScreen(
                        viewModel = viewModel,
                        onNavigateToCore = { viewModel.setTab(0) }
                    )
                }
            }

            // Persona Dialog
            if (showPersonaDialog) {
                PersonaSelectionDialog(
                    activePersona = activePersona,
                    onSelectPersona = {
                        viewModel.setPersona(it)
                        showPersonaDialog = false
                    },
                    onDismiss = { showPersonaDialog = false }
                )
            }
        }
    }
}

@Composable
fun PersonaSelectionDialog(
    activePersona: JarvisPersona,
    onSelectPersona: (JarvisPersona) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        HudCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("persona_dialog"),
            borderColor = JarvisGold
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT AI MATRIX",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = JarvisGoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = JarvisCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                JarvisPersona.entries.forEach { persona ->
                    val isSelected = activePersona == persona
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(CutCornerShape(6.dp))
                            .background(if (isSelected) JarvisCyan.copy(alpha = 0.2f) else JarvisSurface)
                            .border(
                                1.dp,
                                if (isSelected) JarvisCyan else JarvisBorderMuted,
                                CutCornerShape(6.dp)
                            )
                            .clickable { onSelectPersona(persona) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = persona.title.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = if (isSelected) JarvisCyanLight else JarvisTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = persona.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = JarvisTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = JarvisCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
