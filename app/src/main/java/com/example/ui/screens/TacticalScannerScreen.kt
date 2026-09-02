package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderMuted
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

@Composable
fun TacticalScannerScreen(
    viewModel: JarvisViewModel,
    onNavigateToCore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scannedImage by viewModel.scannedImage.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()

    var customPrompt by remember { mutableStateOf("Analyze this visual telemetry and identify key components, sir.") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                }
                viewModel.setScannedImage(bitmap)
            } catch (_: Exception) {}
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ScannerAnim")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_line"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: HUD Visor Optical Viewfinder
        item {
            HudCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("optical_viewfinder_card"),
                borderColor = JarvisCyan
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OPTICAL HUD // TARGETING VISOR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JarvisCyan
                            )
                        )
                        Text(
                            text = "STARK SENSORS V2",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JarvisGoldLight,
                                fontSize = 8.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Viewfinder Screen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(CutCornerShape(8.dp))
                            .background(JarvisSurface)
                            .border(1.dp, JarvisBorderMuted, CutCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (scannedImage != null) {
                            Image(
                                bitmap = scannedImage!!.asImageBitmap(),
                                contentDescription = "Scanned Target",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterCenterFocus,
                                    contentDescription = null,
                                    tint = JarvisCyanLight,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "SELECT SCHEMATIC OR OPTICAL FEED",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = JarvisTextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Tap below to import image from device",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = JarvisTextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Holographic Crosshair & Scanning Overlay Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val centerX = w / 2f
                            val centerY = h / 2f

                            // Central Reticle
                            drawCircle(
                                color = JarvisCyan.copy(alpha = 0.5f),
                                radius = 30.dp.toPx(),
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawLine(
                                color = JarvisCyanLight,
                                start = Offset(centerX - 40.dp.toPx(), centerY),
                                end = Offset(centerX + 40.dp.toPx(), centerY),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = JarvisCyanLight,
                                start = Offset(centerX, centerY - 40.dp.toPx()),
                                end = Offset(centerX, centerY + 40.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Sweeping Holographic Laser Line
                            if (scannedImage != null || isThinking) {
                                val currentY = h * scanLineY
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            JarvisCyanLight,
                                            JarvisCyan,
                                            JarvisCyanLight,
                                            Color.Transparent
                                        )
                                    ),
                                    start = Offset(0f, currentY),
                                    end = Offset(w, currentY),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Media Picker Button
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CutCornerShape(6.dp))
                            .testTag("pick_image_button"),
                        shape = CutCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = JarvisCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JarvisCyan)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (scannedImage != null) "CHANGE TARGET IMAGE" else "SELECT IMAGE / BLUEPRINT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Tactical Scan Prompts
        val scanPrompts = listOf(
            "Identify structural vulnerabilities",
            "Decipher engineering schematic",
            "Calculate thermal & mass parameters",
            "Provide tactical combat assessment"
        )

        item {
            Text(
                text = "TACTICAL SCAN DIRECTIVES",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = JarvisGoldLight,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(scanPrompts) { prompt ->
                    val isSelected = customPrompt == prompt
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(4.dp))
                            .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurface)
                            .border(
                                1.dp,
                                if (isSelected) JarvisCyan else JarvisBorderMuted,
                                CutCornerShape(4.dp)
                            )
                            .clickable { customPrompt = prompt }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = prompt.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) JarvisCyanLight else JarvisTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Execute Analysis Button
        item {
            Button(
                onClick = {
                    if (scannedImage != null) {
                        viewModel.sendUserMessage(customPrompt, scannedImage)
                        onNavigateToCore()
                    }
                },
                enabled = scannedImage != null && !isThinking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(CutCornerShape(8.dp))
                    .testTag("analyze_optical_feed_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JarvisGold,
                    contentColor = JarvisDarkBg
                ),
                shape = CutCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isThinking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = JarvisDarkBg,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ANALYZING SCHEMATIC...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EXECUTE TACTICAL MULTIMODAL SCAN",
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
}
