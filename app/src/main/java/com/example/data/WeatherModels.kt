package com.example.data

enum class FlightSafetyStatus(val label: String, val clearanceColor: Long) {
    OPTIMAL("SUPERSONIC CLEARANCE: GRANTED", 0xFF00E5FF),
    CAUTION("FLIGHT CLEARANCE: SUB-MACH ONLY", 0xFFFFD700),
    HAZARD("ATMOSPHERIC STORM: FLIGHT HAZARD", 0xFFFF3B30)
}

data class HourlyForecastItem(
    val timeLabel: String,
    val tempC: Int,
    val condition: String,
    val icon: String
)

data class StarkWeatherLocation(
    val id: String,
    val name: String,
    val subRegion: String,
    val coordinates: String,
    val tempCelsius: Float,
    val condition: String,
    val conditionSummary: String,
    val feelsLikeCelsius: Float,
    val humidityPercent: Int,
    val windKmh: Float,
    val windDirection: String,
    val pressureHpa: Int,
    val uvIndex: Int,
    val visibilityKm: Float,
    val flightSafety: FlightSafetyStatus,
    val ionChargeDensity: Float,
    val cloudCoverPercent: Int,
    val precipitationChance: Int,
    val hourlyForecast: List<HourlyForecastItem>,
    val microClimateModifierTemp: Float = 0f,
    val isShieldActive: Boolean = false,
    val isCloudSeedingActive: Boolean = false
)

object DefaultWeatherData {
    fun getInitialLocations(): List<StarkWeatherLocation> = listOf(
        StarkWeatherLocation(
            id = "MALIBU",
            name = "Malibu Point",
            subRegion = "California, USA // Stark Estate",
            coordinates = "34.0259° N, 118.7798° W",
            tempCelsius = 22.5f,
            condition = "Clear Sky",
            conditionSummary = "Pacific coastal onshore breeze. Perfect conditions for Mark LXXXV flight test.",
            feelsLikeCelsius = 23.0f,
            humidityPercent = 48,
            windKmh = 14.2f,
            windDirection = "WSW",
            pressureHpa = 1014,
            uvIndex = 6,
            visibilityKm = 25.0f,
            flightSafety = FlightSafetyStatus.OPTIMAL,
            ionChargeDensity = 12.4f,
            cloudCoverPercent = 10,
            precipitationChance = 0,
            hourlyForecast = listOf(
                HourlyForecastItem("08:00", 20, "Clear", "☀️"),
                HourlyForecastItem("11:00", 22, "Sunny", "☀️"),
                HourlyForecastItem("14:00", 24, "Sunny", "☀️"),
                HourlyForecastItem("17:00", 21, "Breeze", "🌤️"),
                HourlyForecastItem("20:00", 18, "Clear", "🌙"),
                HourlyForecastItem("23:00", 16, "Clear", "✨")
            )
        ),
        StarkWeatherLocation(
            id = "NYC",
            name = "Manhattan",
            subRegion = "New York, USA // Avengers Tower",
            coordinates = "40.7580° N, 73.9855° W",
            tempCelsius = 17.8f,
            condition = "Overcast & Ionized Fog",
            conditionSummary = "Urban high-altitude turbulence around top penthouse landing pad.",
            feelsLikeCelsius = 16.5f,
            humidityPercent = 65,
            windKmh = 28.5f,
            windDirection = "ENE",
            pressureHpa = 1008,
            uvIndex = 3,
            visibilityKm = 12.0f,
            flightSafety = FlightSafetyStatus.CAUTION,
            ionChargeDensity = 38.2f,
            cloudCoverPercent = 75,
            precipitationChance = 35,
            hourlyForecast = listOf(
                HourlyForecastItem("08:00", 15, "Cloudy", "☁️"),
                HourlyForecastItem("11:00", 17, "Overcast", "☁️"),
                HourlyForecastItem("14:00", 19, "Light Rain", "🌦️"),
                HourlyForecastItem("17:00", 18, "Overcast", "☁️"),
                HourlyForecastItem("20:00", 16, "Fog", "🌫️"),
                HourlyForecastItem("23:00", 14, "Clear", "🌙")
            )
        ),
        StarkWeatherLocation(
            id = "TOKYO",
            name = "Tokyo Bay",
            subRegion = "Japan // Stark R&D Complex",
            coordinates = "35.6762° N, 139.6503° E",
            tempCelsius = 25.4f,
            condition = "High Altitude Thunderstorm",
            conditionSummary = "Electromagnetic lightning field active. Shield polarity advised.",
            feelsLikeCelsius = 27.1f,
            humidityPercent = 82,
            windKmh = 45.0f,
            windDirection = "SSE",
            pressureHpa = 996,
            uvIndex = 2,
            visibilityKm = 6.5f,
            flightSafety = FlightSafetyStatus.HAZARD,
            ionChargeDensity = 89.6f,
            cloudCoverPercent = 95,
            precipitationChance = 85,
            hourlyForecast = listOf(
                HourlyForecastItem("08:00", 23, "Thunder", "⛈️"),
                HourlyForecastItem("11:00", 25, "Heavy Rain", "🌧️"),
                HourlyForecastItem("14:00", 26, "Storm", "⚡"),
                HourlyForecastItem("17:00", 24, "Showers", "🌦️"),
                HourlyForecastItem("20:00", 22, "Cloudy", "☁️"),
                HourlyForecastItem("23:00", 21, "Overcast", "☁️")
            )
        ),
        StarkWeatherLocation(
            id = "LONDON",
            name = "London Orbital",
            subRegion = "UK // Stark Satellite Uplink Station",
            coordinates = "51.5074° N, 0.1278° W",
            tempCelsius = 14.0f,
            condition = "Misty Drizzle",
            conditionSummary = "Low cloud ceiling, mild barometric depression.",
            feelsLikeCelsius = 13.2f,
            humidityPercent = 78,
            windKmh = 18.0f,
            windDirection = "NW",
            pressureHpa = 1012,
            uvIndex = 2,
            visibilityKm = 10.0f,
            flightSafety = FlightSafetyStatus.OPTIMAL,
            ionChargeDensity = 21.0f,
            cloudCoverPercent = 80,
            precipitationChance = 40,
            hourlyForecast = listOf(
                HourlyForecastItem("08:00", 12, "Drizzle", "🌦️"),
                HourlyForecastItem("11:00", 14, "Misty", "🌫️"),
                HourlyForecastItem("14:00", 15, "Cloudy", "☁️"),
                HourlyForecastItem("17:00", 14, "Drizzle", "🌦️"),
                HourlyForecastItem("20:00", 12, "Cloudy", "☁️"),
                HourlyForecastItem("23:00", 11, "Clear", "🌙")
            )
        ),
        StarkWeatherLocation(
            id = "STRATOSPHERE",
            name = "Sub-Orbital 80,000ft",
            subRegion = "High Altitude Atmospheric Layer",
            coordinates = "37.7749° N, 122.4194° W // FL800",
            tempCelsius = -52.0f,
            condition = "Icing Conditions & Solar Wind",
            conditionSummary = "Sub-zero icing risk. Arc Reactor thermal heater required for suit de-icing.",
            feelsLikeCelsius = -68.0f,
            humidityPercent = 5,
            windKmh = 180.0f,
            windDirection = "W",
            pressureHpa = 280,
            uvIndex = 14,
            visibilityKm = 150.0f,
            flightSafety = FlightSafetyStatus.CAUTION,
            ionChargeDensity = 94.0f,
            cloudCoverPercent = 0,
            precipitationChance = 0,
            hourlyForecast = listOf(
                HourlyForecastItem("08:00", -54, "Icing", "❄️"),
                HourlyForecastItem("11:00", -50, "Solar UV", "☀️"),
                HourlyForecastItem("14:00", -48, "Jet Stream", "💨"),
                HourlyForecastItem("17:00", -52, "Aurora", "🌌"),
                HourlyForecastItem("20:00", -55, "Deep Space", "✨"),
                HourlyForecastItem("23:00", -58, "Vacuum Border", "⭐")
            )
        )
    )
}
