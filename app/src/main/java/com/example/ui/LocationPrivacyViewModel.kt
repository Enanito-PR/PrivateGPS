package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocationPreferences
import com.example.data.WorldCitiesRepository
import com.example.model.CityLocation
import com.example.model.MovementMode
import com.example.model.SimulationState
import com.example.service.LocationPrivacyService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LocationPrivacyViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = LocationPreferences(application)

    // Flow directly reflecting the background service simulation state
    val simulationState: StateFlow<SimulationState> = LocationPrivacyService.simulationState

    // City Catalog & Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedContinent = MutableStateFlow("Todos")
    val selectedContinent = _selectedContinent.asStateFlow()

    private val _customCities = MutableStateFlow<List<CityLocation>>(emptyList())

    val filteredCities: StateFlow<List<CityLocation>> = combine(
        _searchQuery,
        _selectedContinent,
        _customCities
    ) { query, continent, customList ->
        val allCities = customList + WorldCitiesRepository.defaultCities
        allCities.filter { city ->
            val matchesContinent = continent == "Todos" || city.continent.equals(continent, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    city.name.contains(query, ignoreCase = true) ||
                    city.country.contains(query, ignoreCase = true)
            matchesContinent && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorldCitiesRepository.defaultCities)

    init {
        // Pre-load saved state if service is not currently active
        if (!LocationPrivacyService.isRunning) {
            val saved = preferences.loadSimulationState()
            // Just ensure initial values exist
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onContinentSelected(continent: String) {
        _selectedContinent.value = continent
    }

    fun toggleService() {
        val current = simulationState.value
        if (current.isActive) {
            stopService()
        } else {
            startService(current.latitude, current.longitude, current.currentCity, current.currentCountry, current.mode, current.speedKmh)
        }
    }

    fun startWithCity(city: CityLocation) {
        val current = simulationState.value
        startService(
            lat = city.latitude,
            lng = city.longitude,
            city = city.name,
            country = city.country,
            mode = current.mode,
            speed = current.speedKmh
        )
    }

    fun setCustomCoordinates(lat: Double, lng: Double, name: String, country: String = "Personalizada") {
        val newCity = CityLocation(
            id = "custom_${System.currentTimeMillis()}",
            name = name.ifBlank { "Punto Personalizado" },
            country = country,
            continent = "Personalizado",
            latitude = lat,
            longitude = lng,
            altitude = 100.0,
            flagEmoji = "🎯"
        )
        _customCities.value = listOf(newCity) + _customCities.value
        startWithCity(newCity)
    }

    private fun startService(
        lat: Double,
        lng: Double,
        city: String,
        country: String,
        mode: MovementMode,
        speed: Float
    ) {
        val context = getApplication<Application>()
        val intent = Intent(context, LocationPrivacyService::class.java).apply {
            action = LocationPrivacyService.ACTION_START
            putExtra(LocationPrivacyService.EXTRA_LATITUDE, lat)
            putExtra(LocationPrivacyService.EXTRA_LONGITUDE, lng)
            putExtra(LocationPrivacyService.EXTRA_CITY, city)
            putExtra(LocationPrivacyService.EXTRA_COUNTRY, country)
            putExtra(LocationPrivacyService.EXTRA_MODE, mode.name)
            putExtra(LocationPrivacyService.EXTRA_SPEED, speed)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopService() {
        val context = getApplication<Application>()
        val intent = Intent(context, LocationPrivacyService::class.java).apply {
            action = LocationPrivacyService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun setMovementMode(mode: MovementMode) {
        val current = simulationState.value
        val newSpeed = mode.defaultSpeedKmh
        sendConfigUpdate(mode = mode, speed = newSpeed)
    }

    fun setSpeed(speedKmh: Float) {
        sendConfigUpdate(speed = speedKmh)
    }

    private fun sendConfigUpdate(
        lat: Double? = null,
        lng: Double? = null,
        city: String? = null,
        country: String? = null,
        mode: MovementMode? = null,
        speed: Float? = null
    ) {
        val context = getApplication<Application>()
        if (LocationPrivacyService.isRunning) {
            val intent = Intent(context, LocationPrivacyService::class.java).apply {
                action = LocationPrivacyService.ACTION_UPDATE_CONFIG
                lat?.let { putExtra(LocationPrivacyService.EXTRA_LATITUDE, it) }
                lng?.let { putExtra(LocationPrivacyService.EXTRA_LONGITUDE, it) }
                city?.let { putExtra(LocationPrivacyService.EXTRA_CITY, it) }
                country?.let { putExtra(LocationPrivacyService.EXTRA_COUNTRY, it) }
                mode?.let { putExtra(LocationPrivacyService.EXTRA_MODE, it.name) }
                speed?.let { putExtra(LocationPrivacyService.EXTRA_SPEED, it) }
            }
            context.startService(intent)
        } else {
            // Update preferences directly
            val current = simulationState.value
            val updated = current.copy(
                latitude = lat ?: current.latitude,
                longitude = lng ?: current.longitude,
                currentCity = city ?: current.currentCity,
                currentCountry = country ?: current.currentCountry,
                mode = mode ?: current.mode,
                speedKmh = speed ?: current.speedKmh
            )
            preferences.saveSimulationState(updated)
        }
    }

    fun steerManual(angle: Float) {
        val context = getApplication<Application>()
        if (LocationPrivacyService.isRunning) {
            val intent = Intent(context, LocationPrivacyService::class.java).apply {
                action = LocationPrivacyService.ACTION_MANUAL_STEER
                putExtra(LocationPrivacyService.EXTRA_STEER_ANGLE, angle)
            }
            context.startService(intent)
        }
    }

    fun setPersistOnBoot(enabled: Boolean) {
        preferences.setPersistOnBoot(enabled)
        val current = simulationState.value
        sendConfigUpdate()
    }

    fun openDeveloperOptions(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
