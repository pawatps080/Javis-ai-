package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JarvisPersona
import com.example.ui.JarvisViewModel
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisBorderMuted
import com.example.ui.theme.JarvisCrimson
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDark
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldLight
import com.example.ui.theme.JarvisPanelBgSemi
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

data class ProtocolItem(
    val id: String,
    val title: String,
    val codeName: String,
    val description: String,
    val icon: ImageVector,
    val isDangerous: Boolean = false
)

@Composable
fun ProtocolArchiveScreen(
    viewModel: JarvisViewModel,
    onNavigateToCore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePersona by viewModel.persona.collectAsState()

    val protocols = listOf(
        ProtocolItem(
            id = "HOUSE_PARTY",
            title = "HOUSE PARTY PROTOCOL",
            codeName = "IRON LEGION MK VIII - XLI",
            description = "Dispatches all autonomous armor units from subterranean facility to current coordinates.",
            icon = Icons.Default.Hub
        ),
        ProtocolItem(
            id = "VERONICA",
            title = "VERONICA DEPLOYMENT",
            codeName = "MARK XLIV HULKBUSTER",
            description = "Arms orbital supply capsule and deploys heavy armor chassis with hydraulic cages.",
            icon = Icons.Default.RocketLaunch
        ),
        ProtocolItem(
            id = "IGOR_LIFT",
            title = "IGOR HEAVY LOAD",
            codeName = "MARK XXXVIII",
            description = "Engages heavy structural support exo-rig with quadruple pneumatic lifters.",
            icon = Icons.Default.Engineering
        ),
        ProtocolItem(
            id = "STEALTH_MODE",
            title = "SNEAKY CLOAKING",
            codeName = "MARK XV",
            description = "Activates retro-reflective radar-absorbent plating and silent repulsor damping.",
            icon = Icons.Default.VisibilityOff
        ),
        ProtocolItem(
            id = "CLEAN_SLATE",
            title = "CLEAN SLATE PROTOCOL",
            codeName = "PURGE SEQUENCE",
            description = "Deconstructs all current armor platforms. Requires executive authorization.",
            icon = Icons.Default.DeleteSweep,
            isDangerous = true
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: AI Persona Selector
        item {
            HudCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("persona_matrix_card"),
                borderColor = JarvisGold
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = JarvisGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "J.A.R.V.I.S. NEURAL MATRIX",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JarvisGoldLight
                                )
                            )
                        }

                        Text(
                            text = "GEMINI CORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisTextMuted,
                                fontSize = 8.sp
                            )
                        )
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
                                .clickable { viewModel.setPersona(persona) }
                                .padding(12.dp)
                                .testTag("persona_item_${persona.name}")
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

        // Section 2: Stark Protocols List
        item {
            Text(
                text = "EXECUTIVE ARMOR DIRECTIVES",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = JarvisCyanLight,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        items(protocols, key = { it.id }) { item ->
            HudCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("protocol_card_${item.id}"),
                borderColor = if (item.isDangerous) JarvisCrimson else JarvisBorderMuted
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (item.isDangerous) JarvisCrimson else JarvisCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isDangerous) JarvisCrimson else JarvisTextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = item.codeName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = JarvisGoldLight,
                                        fontSize = 8.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.triggerProtocol(item.id)
                                onNavigateToCore()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.isDangerous) JarvisCrimson else JarvisCyan,
                                contentColor = JarvisDarkBg
                            ),
                            shape = CutCornerShape(4.dp),
                            modifier = Modifier.testTag("execute_${item.id}_btn")
                        ) {
                            Text(
                                text = "EXECUTE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = JarvisTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
