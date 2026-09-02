package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketAsset
import com.example.data.MarketCategory
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
fun MarketsScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val marketAssets by viewModel.marketAssets.collectAsState()
    val selectedCategory by viewModel.selectedMarketCategory.collectAsState()
    val isRefreshing by viewModel.isMarketsRefreshing.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedAssetForDetail by remember { mutableStateOf<MarketAsset?>(null) }

    val filteredAssets = remember(marketAssets, selectedCategory, searchQuery) {
        marketAssets.filter { asset ->
            val matchesCategory = selectedCategory == MarketCategory.ALL || asset.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    asset.symbol.contains(searchQuery, ignoreCase = true) ||
                    asset.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val activeHeroAsset = selectedAssetForDetail ?: marketAssets.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("markets_screen")
    ) {
        // Top Ticker Header Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CurrencyBitcoin,
                        contentDescription = "Market Icon",
                        tint = JarvisGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GLOBAL FINANCIAL & CRYPTO MATRIX",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 14.sp
                        )
                    )
                }
                Text(
                    text = "STARK QUANTUM SATELLITE FEED // REAL-TIME TELEMETRY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JarvisGoldLight,
                        fontSize = 9.sp
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.refreshMarketData() },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("refresh_markets_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Market Telemetry",
                        tint = if (isRefreshing) JarvisGold else JarvisCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Asset Sparkline Chart Card
        if (activeHeroAsset != null) {
            HeroMarketChartCard(
                asset = activeHeroAsset,
                onAskJarvis = {
                    viewModel.sendUserMessage("Jarvis, provide a comprehensive market analysis and projection for ${activeHeroAsset.name} (${activeHeroAsset.symbol}).")
                    viewModel.setTab(0)
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar & Query Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("market_search_input"),
            placeholder = {
                Text(
                    text = "Search Bitcoin, S&P 500, NASDAQ, Solana...",
                    style = MaterialTheme.typography.bodySmall.copy(color = JarvisTextMuted)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = JarvisCyan,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            shape = CutCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JarvisCyan,
                unfocusedBorderColor = JarvisBorderMuted,
                focusedContainerColor = JarvisSurface,
                unfocusedContainerColor = JarvisSurface,
                focusedTextColor = JarvisTextPrimary,
                unfocusedTextColor = JarvisTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MarketCategory.entries.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurface)
                        .border(
                            1.dp,
                            if (isSelected) JarvisCyan else JarvisBorderMuted,
                            CutCornerShape(4.dp)
                        )
                        .clickable { viewModel.setMarketCategory(category) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("filter_tab_${category.name.lowercase()}")
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) JarvisCyanLight else JarvisTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Asset List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredAssets, key = { it.symbol }) { asset ->
                MarketAssetRow(
                    asset = asset,
                    isSelected = activeHeroAsset?.symbol == asset.symbol,
                    onClick = { selectedAssetForDetail = asset }
                )
            }
        }
    }
}

@Composable
fun HeroMarketChartCard(
    asset: MarketAsset,
    onAskJarvis: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = asset.changePercent >= 0
    val trendColor = if (isPositive) JarvisCyan else JarvisRed

    HudCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_market_card"),
        borderColor = if (isPositive) JarvisCyan else JarvisGold
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = asset.symbol,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = JarvisTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(trendColor.copy(alpha = 0.15f))
                                .border(1.dp, trendColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = trendColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = String.format(Locale.US, "%+.2f%%", asset.changePercent),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = trendColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = JarvisTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "$%,.2f", asset.price),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = String.format(Locale.US, "%+,.2f today", asset.changeAmount),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isPositive) JarvisCyanLight else JarvisRed,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Holographic Line Chart Canvas
            HolographicSparkline(
                dataPoints = asset.sparkline,
                lineColor = trendColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Key Telemetry Row: 24h High, 24h Low, Market Cap, 24h Volume
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JarvisDarkBg.copy(alpha = 0.6f))
                    .border(1.dp, JarvisBorderMuted, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryMetricItem(title = "24H HIGH", value = String.format(Locale.US, "$%,.1f", asset.high24h))
                TelemetryMetricItem(title = "24H LOW", value = String.format(Locale.US, "$%,.1f", asset.low24h))
                TelemetryMetricItem(title = "24H VOL", value = asset.volume)
                TelemetryMetricItem(title = "MCAP", value = asset.marketCap)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ask Jarvis AI Analysis Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(4.dp))
                    .background(JarvisCyan.copy(alpha = 0.12f))
                    .border(1.dp, JarvisCyan, CutCornerShape(4.dp))
                    .clickable(onClick = onAskJarvis)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .testTag("ask_jarvis_market_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Analysis",
                        tint = JarvisCyanLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REQUEST JARVIS AI FINANCIAL REPORT",
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

@Composable
fun TelemetryMetricItem(title: String, value: String) {
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

@Composable
fun HolographicSparkline(
    dataPoints: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (dataPoints.size < 2) return@Canvas

        val minVal = dataPoints.minOrNull() ?: 0f
        val maxVal = dataPoints.maxOrNull() ?: 1f
        val range = if (maxVal == minVal) 1f else maxVal - minVal

        val width = size.width
        val height = size.height
        val stepX = width / (dataPoints.size - 1)

        val path = Path()
        val fillPath = Path()

        val points = dataPoints.mapIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minVal) / range) * (height * 0.8f) - (height * 0.1f)
            Offset(x, y)
        }

        path.moveTo(points.first().x, points.first().y)
        fillPath.moveTo(points.first().x, height)
        fillPath.lineTo(points.first().x, points.first().y)

        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            val midX = (prev.x + curr.x) / 2
            path.cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
            fillPath.cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
        }

        fillPath.lineTo(points.last().x, height)
        fillPath.close()

        // Draw glowing gradient underneath
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Draw crisp line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw dots at key vertices
        points.forEach { pt ->
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = lineColor,
                radius = 4.5.dp.toPx(),
                center = pt,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

@Composable
fun MarketAssetRow(
    asset: MarketAsset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = asset.changePercent >= 0
    val trendColor = if (isPositive) JarvisCyan else JarvisRed

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CutCornerShape(6.dp))
            .background(if (isSelected) JarvisCyan.copy(alpha = 0.15f) else JarvisSurface)
            .border(
                1.dp,
                if (isSelected) JarvisCyan else JarvisBorderMuted,
                CutCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("asset_row_${asset.symbol.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(JarvisDarkBg)
                        .border(1.dp, trendColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = asset.symbol.take(4),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JarvisCyanLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = asset.symbol,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = JarvisTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = JarvisTextSecondary,
                            fontSize = 10.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            // Mini Sparkline preview
            HolographicSparkline(
                dataPoints = asset.sparkline,
                lineColor = trendColor,
                modifier = Modifier
                    .width(70.dp)
                    .height(30.dp)
                    .padding(horizontal = 4.dp)
            )

            // Price & Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "$%,.2f", asset.price),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JarvisTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = String.format(Locale.US, "%+.2f%%", asset.changePercent),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = trendColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
