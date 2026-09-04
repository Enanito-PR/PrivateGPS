package com.example.model

enum class MovementMode(
    val title: String,
    val description: String,
    val defaultSpeedKmh: Float,
    val iconName: String
) {
    JITTER_STATIC(
        title = "Estático con Deriva Natural",
        description = "Simula fluctuaciones mínimas de GPS (1-3m) para evitar que detecten un punto congelado artificial.",
        defaultSpeedKmh = 0.5f,
        iconName = "PinDrop"
    ),
    WALKING(
        title = "Paseo Peatonal",
        description = "Desplazamiento a velocidad humana (~5 km/h) simulando caminata por calles.",
        defaultSpeedKmh = 4.8f,
        iconName = "DirectionsWalk"
    ),
    CYCLING(
        title = "Bicicleta Urbana",
        description = "Movimiento fluido a velocidad media (~18 km/h).",
        defaultSpeedKmh = 18.0f,
        iconName = "DirectionsBike"
    ),
    DRIVING(
        title = "Vehículo / Coche",
        description = "Desplazamiento rápido (~50 km/h) con cambios de dirección suaves.",
        defaultSpeedKmh = 50.0f,
        iconName = "DirectionsCar"
    ),
    SMART_PATROL(
        title = "Patrulla en Bucle",
        description = "Recorre un circuito cerrado alrededor de puntos de interés de la ciudad.",
        defaultSpeedKmh = 25.0f,
        iconName = "Sync"
    )
}

data class CityLocation(
    val id: String,
    val name: String,
    val country: String,
    val continent: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 35.0,
    val flagEmoji: String = "📍"
)

data class SimulationState(
    val isActive: Boolean = false,
    val currentCity: String = "Madrid",
    val currentCountry: String = "España",
    val latitude: Double = 40.4168,
    val longitude: Double = -3.7038,
    val altitude: Double = 650.0,
    val speedKmh: Float = 4.8f,
    val bearing: Float = 0f,
    val accuracyMeters: Float = 4.5f,
    val mode: MovementMode = MovementMode.WALKING,
    val isMockProviderActive: Boolean = false,
    val mockProviderError: String? = null,
    val distanceTraveledMeters: Double = 0.0,
    val elapsedSeconds: Long = 0L,
    val persistOnBoot: Boolean = true,
    val naturalJitterEnabled: Boolean = true
)
