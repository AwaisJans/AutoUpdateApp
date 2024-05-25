package com.jans.tiles.app.weather

data class WeatherData(
    val name: String,
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double
)

data class Weather(
    val icon: String
)