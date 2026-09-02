package com.example.data

enum class MarketCategory(val label: String) {
    ALL("ALL ASSETS"),
    INDICES("GLOBAL INDICES"),
    CRYPTO("CRYPTO CORE"),
    STARK("STARK TECH")
}

data class MarketAsset(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val category: MarketCategory,
    val sparkline: List<Float>,
    val high24h: Double,
    val low24h: Double,
    val volume: String,
    val marketCap: String,
    val isCustom: Boolean = false
)

object DefaultMarketData {
    fun getInitialAssets(): List<MarketAsset> = listOf(
        MarketAsset(
            symbol = "BTC",
            name = "Bitcoin Core",
            price = 89420.50,
            changePercent = 4.38,
            changeAmount = 3750.20,
            category = MarketCategory.CRYPTO,
            sparkline = listOf(84200f, 85100f, 84800f, 86300f, 87400f, 86900f, 88200f, 89420f),
            high24h = 90150.00,
            low24h = 84020.00,
            volume = "$48.2B",
            marketCap = "$1.76T"
        ),
        MarketAsset(
            symbol = "STARK",
            name = "Stark Industries Inc.",
            price = 3845.20,
            changePercent = 6.72,
            changeAmount = 242.10,
            category = MarketCategory.STARK,
            sparkline = listOf(3550f, 3590f, 3620f, 3700f, 3680f, 3750f, 3810f, 3845f),
            high24h = 3890.00,
            low24h = 3540.00,
            volume = "$14.8B",
            marketCap = "$840.5B"
        ),
        MarketAsset(
            symbol = "ETH",
            name = "Ethereum Protocol",
            price = 3452.80,
            changePercent = 3.15,
            changeAmount = 105.40,
            category = MarketCategory.CRYPTO,
            sparkline = listOf(3290f, 3310f, 3350f, 3320f, 3380f, 3410f, 3430f, 3452f),
            high24h = 3520.00,
            low24h = 3280.00,
            volume = "$22.4B",
            marketCap = "$415.2B"
        ),
        MarketAsset(
            symbol = "^GSPC",
            name = "S&P 500 Index",
            price = 5894.30,
            changePercent = 0.82,
            changeAmount = 48.10,
            category = MarketCategory.INDICES,
            sparkline = listOf(5810f, 5825f, 5840f, 5835f, 5860f, 5875f, 5885f, 5894f),
            high24h = 5910.00,
            low24h = 5805.00,
            volume = "$4.2B",
            marketCap = "$44.8T"
        ),
        MarketAsset(
            symbol = "^IXIC",
            name = "NASDAQ Composite",
            price = 18520.40,
            changePercent = 1.45,
            changeAmount = 265.10,
            category = MarketCategory.INDICES,
            sparkline = listOf(18100f, 18220f, 18180f, 18340f, 18400f, 18460f, 18500f, 18520f),
            high24h = 18580.00,
            low24h = 18050.00,
            volume = "$6.1B",
            marketCap = "$26.2T"
        ),
        MarketAsset(
            symbol = "^DJI",
            name = "Dow Jones Industrial",
            price = 42180.60,
            changePercent = -0.24,
            changeAmount = -101.30,
            category = MarketCategory.INDICES,
            sparkline = listOf(42400f, 42350f, 42300f, 42210f, 42250f, 42190f, 42150f, 42180f),
            high24h = 42450.00,
            low24h = 42080.00,
            volume = "$3.8B",
            marketCap = "$14.5T"
        ),
        MarketAsset(
            symbol = "SOL",
            name = "Solana Network",
            price = 196.40,
            changePercent = 7.82,
            changeAmount = 14.25,
            category = MarketCategory.CRYPTO,
            sparkline = listOf(178f, 180f, 183f, 182f, 188f, 191f, 194f, 196f),
            high24h = 202.00,
            low24h = 176.50,
            volume = "$8.9B",
            marketCap = "$91.4B"
        ),
        MarketAsset(
            symbol = "VIB",
            name = "Vibranium Futures",
            price = 14250.00,
            changePercent = 11.40,
            changeAmount = 1460.00,
            category = MarketCategory.STARK,
            sparkline = listOf(12500f, 12700f, 12900f, 13400f, 13200f, 13800f, 14000f, 14250f),
            high24h = 14500.00,
            low24h = 12400.00,
            volume = "$940M",
            marketCap = "$120.0B"
        ),
        MarketAsset(
            symbol = "ARC",
            name = "Arc Energy Clean Index",
            price = 824.50,
            changePercent = 5.20,
            changeAmount = 40.70,
            category = MarketCategory.STARK,
            sparkline = listOf(760f, 770f, 785f, 780f, 800f, 810f, 818f, 824f),
            high24h = 835.00,
            low24h = 755.00,
            volume = "$1.2B",
            marketCap = "$45.0B"
        )
    )
}
