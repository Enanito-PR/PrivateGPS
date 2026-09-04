package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.LocationPreferences
import com.example.model.MovementMode
import com.example.model.SimulationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class LocationPrivacyService : Service() {

    companion object {
        const val ACTION_START = "com.example.service.ACTION_START"
        const val ACTION_STOP = "com.example.service.ACTION_STOP"
        const val ACTION_UPDATE_CONFIG = "com.example.service.ACTION_UPDATE_CONFIG"
        const val ACTION_MANUAL_STEER = "com.example.service.ACTION_MANUAL_STEER"

        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
        const val EXTRA_CITY = "extra_city"
        const val EXTRA_COUNTRY = "extra_country"
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_SPEED = "extra_speed"
        const val EXTRA_STEER_ANGLE = "extra_steer_angle"

        private const val CHANNEL_ID = "location_privacy_channel"
        private const val NOTIFICATION_ID = 4091

        private val _simulationState = MutableStateFlow(SimulationState())
        val simulationState: StateFlow<SimulationState> = _simulationState.asStateFlow()

        var isRunning: Boolean = false
            private set
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var simulationJob: Job? = null
    private lateinit var locationManager: LocationManager
    private lateinit var preferences: LocationPreferences
    private var baseLatitude: Double = 40.4168
    private var baseLongitude: Double = -3.7038
    private var currentLatitude: Double = 40.4168
    private var currentLongitude: Double = -3.7038
    private var currentBearing: Float = 0f
    private var totalDistanceTraveled: Double = 0.0
    private var sessionStartTime: Long = 0L

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        preferences = LocationPreferences(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSimulationService()
                return START_NOT_STICKY
            }
            ACTION_UPDATE_CONFIG -> {
                updateConfiguration(intent)
            }
            ACTION_MANUAL_STEER -> {
                handleManualSteer(intent)
            }
            ACTION_START, null -> {
                val lat = intent?.getDoubleExtra(EXTRA_LATITUDE, Double.NaN) ?: Double.NaN
                val lng = intent?.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN) ?: Double.NaN
                val city = intent?.getStringExtra(EXTRA_CITY)
                val country = intent?.getStringExtra(EXTRA_COUNTRY)
                val modeStr = intent?.getStringExtra(EXTRA_MODE)
                val speed = intent?.getFloatExtra(EXTRA_SPEED, -1f) ?: -1f

                startSimulation(lat, lng, city, country, modeStr, speed)
            }
        }
        return START_STICKY
    }

    private fun startSimulation(
        lat: Double,
        lng: Double,
        city: String?,
        country: String?,
        modeStr: String?,
        speed: Float
    ) {
        val saved = preferences.loadSimulationState()
        val targetLat = if (!lat.isNaN()) lat else saved.latitude
        val targetLng = if (!lng.isNaN()) lng else saved.longitude
        val targetCity = city ?: saved.currentCity
        val targetCountry = country ?: saved.currentCountry
        val targetMode = if (modeStr != null) {
            try { MovementMode.valueOf(modeStr) } catch (_: Exception) { saved.mode }
        } else saved.mode
        val targetSpeed = if (speed > 0) speed else targetMode.defaultSpeedKmh

        baseLatitude = targetLat
        baseLongitude = targetLng
        currentLatitude = targetLat
        currentLongitude = targetLng
        currentBearing = saved.bearing
        totalDistanceTraveled = 0.0
        sessionStartTime = System.currentTimeMillis()

        isRunning = true

        // Promote to foreground service
        val notification = buildNotification(targetCity, currentLatitude, currentLongitude, targetSpeed)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Initialize mock provider
        val setupResult = setupMockLocationProviders()

        _simulationState.value = SimulationState(
            isActive = true,
            currentCity = targetCity,
            currentCountry = targetCountry,
            latitude = currentLatitude,
            longitude = currentLongitude,
            speedKmh = targetSpeed,
            bearing = currentBearing,
            mode = targetMode,
            isMockProviderActive = setupResult.first,
            mockProviderError = setupResult.second,
            persistOnBoot = preferences.isPersistOnBoot()
        )

        preferences.setActive(true)
        preferences.saveSimulationState(_simulationState.value)

        // Start simulation loop
        startLoop()
    }

    private fun updateConfiguration(intent: Intent) {
        val lat = intent.getDoubleExtra(EXTRA_LATITUDE, Double.NaN)
        val lng = intent.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN)
        val city = intent.getStringExtra(EXTRA_CITY)
        val country = intent.getStringExtra(EXTRA_COUNTRY)
        val modeStr = intent.getStringExtra(EXTRA_MODE)
        val speed = intent.getFloatExtra(EXTRA_SPEED, -1f)

        val currentState = _simulationState.value
        val newCity = city ?: currentState.currentCity
        val newCountry = country ?: currentState.currentCountry
        val newMode = if (modeStr != null) {
            try { MovementMode.valueOf(modeStr) } catch (_: Exception) { currentState.mode }
        } else currentState.mode
        val newSpeed = if (speed > 0) speed else newMode.defaultSpeedKmh

        if (!lat.isNaN() && !lng.isNaN()) {
            baseLatitude = lat
            baseLongitude = lng
            currentLatitude = lat
            currentLongitude = lng
        }

        _simulationState.value = currentState.copy(
            currentCity = newCity,
            currentCountry = newCountry,
            latitude = currentLatitude,
            longitude = currentLongitude,
            mode = newMode,
            speedKmh = newSpeed
        )
        preferences.saveSimulationState(_simulationState.value)
        updateNotification()
    }

    private fun handleManualSteer(intent: Intent) {
        val angle = intent.getFloatExtra(EXTRA_STEER_ANGLE, 0f)
        currentBearing = (angle + 360) % 360
        val currentState = _simulationState.value
        val speedMps = (currentState.speedKmh * 1000f) / 3600f
        val stepMeters = (speedMps * 2.5).coerceAtLeast(5.0) // interactive step
        val (nextLat, nextLng) = calculateDestinationPoint(currentLatitude, currentLongitude, stepMeters, currentBearing.toDouble())
        currentLatitude = nextLat
        currentLongitude = nextLng
        totalDistanceTraveled += stepMeters

        _simulationState.value = currentState.copy(
            latitude = currentLatitude,
            longitude = currentLongitude,
            bearing = currentBearing,
            distanceTraveledMeters = totalDistanceTraveled
        )
        pushMockLocation(currentLatitude, currentLongitude, currentState.speedKmh, currentBearing)
    }

    private fun startLoop() {
        simulationJob?.cancel()
        simulationJob = serviceScope.launch {
            var stepCounter = 0
            while (isActive) {
                val state = _simulationState.value
                val elapsed = (System.currentTimeMillis() - sessionStartTime) / 1000L

                // Calculate next coordinates based on mode
                when (state.mode) {
                    MovementMode.JITTER_STATIC -> {
                        // Natural micro-drift: 0.8 to 2.5 meters variance
                        val driftDist = Random.nextDouble(0.5, 2.0)
                        val driftBearing = Random.nextDouble(0.0, 360.0)
                        val (driftLat, driftLng) = calculateDestinationPoint(baseLatitude, baseLongitude, driftDist, driftBearing)
                        currentLatitude = driftLat
                        currentLongitude = driftLng
                    }
                    MovementMode.WALKING, MovementMode.CYCLING, MovementMode.DRIVING -> {
                        val speedMps = (state.speedKmh * 1000.0) / 3600.0
                        val intervalSec = 1.0
                        val stepDistance = speedMps * intervalSec

                        // Smooth street steering: slight angular adjustment with occasional intersection turns
                        if (stepCounter % 8 == 0 && Random.nextFloat() > 0.6f) {
                            val turnDelta = if (Random.nextBoolean()) 90f else -90f
                            currentBearing = (currentBearing + turnDelta + 360f) % 360f
                        } else {
                            val microSwerve = Random.nextFloat() * 6f - 3f
                            currentBearing = (currentBearing + microSwerve + 360f) % 360f
                        }

                        val (nextLat, nextLng) = calculateDestinationPoint(currentLatitude, currentLongitude, stepDistance, currentBearing.toDouble())
                        currentLatitude = nextLat
                        currentLongitude = nextLng
                        totalDistanceTraveled += stepDistance
                    }
                    MovementMode.SMART_PATROL -> {
                        // Patrol in a wide loop around base coordinates
                        val speedMps = (state.speedKmh * 1000.0) / 3600.0
                        val stepDistance = speedMps * 1.0
                        currentBearing = (currentBearing + 4.5f) % 360f // creates a circular orbit
                        val (nextLat, nextLng) = calculateDestinationPoint(currentLatitude, currentLongitude, stepDistance, currentBearing.toDouble())
                        currentLatitude = nextLat
                        currentLongitude = nextLng
                        totalDistanceTraveled += stepDistance
                    }
                }

                stepCounter++

                // Push location to system mock provider
                val pushSuccess = pushMockLocation(
                    lat = currentLatitude,
                    lng = currentLongitude,
                    speedKmh = state.speedKmh,
                    bearing = currentBearing
                )

                _simulationState.value = state.copy(
                    latitude = currentLatitude,
                    longitude = currentLongitude,
                    bearing = currentBearing,
                    distanceTraveledMeters = totalDistanceTraveled,
                    elapsedSeconds = elapsed,
                    isMockProviderActive = pushSuccess.first,
                    mockProviderError = pushSuccess.second
                )

                // Update notification every 5 seconds to minimize battery/system burden
                if (stepCounter % 5 == 0) {
                    updateNotification()
                }

                delay(1000L)
            }
        }
    }

    private fun setupMockLocationProviders(): Pair<Boolean, String?> {
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        var errorEncountered: String? = null

        for (provider in providers) {
            try {
                // Try removing existing provider if leftover
                try {
                    locationManager.removeTestProvider(provider)
                } catch (_: Exception) {}

                locationManager.addTestProvider(
                    provider,
                    false, // requiresNetwork
                    false, // requiresSatellite
                    false, // requiresCell
                    false, // hasMonetaryCost
                    true,  // supportsAltitude
                    true,  // supportsSpeed
                    true,  // supportsBearing
                    ProviderProperties.POWER_USAGE_LOW,
                    ProviderProperties.ACCURACY_FINE
                )
                locationManager.setTestProviderEnabled(provider, true)
            } catch (se: SecurityException) {
                errorEncountered = "Debes seleccionar esta app en 'Opciones de desarrollador' -> 'Elegir aplicación para simular ubicación'."
                return Pair(false, errorEncountered)
            } catch (e: Exception) {
                errorEncountered = e.localizedMessage ?: "Error inicializando proveedor de prueba."
            }
        }
        return Pair(errorEncountered == null, errorEncountered)
    }

    private fun pushMockLocation(lat: Double, lng: Double, speedKmh: Float, bearing: Float): Pair<Boolean, String?> {
        val speedMps = (speedKmh * 1000f) / 3600f
        val accuracy = if (_simulationState.value.naturalJitterEnabled) {
            Random.nextFloat() * 2.5f + 3.5f
        } else {
            4.0f
        }

        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        var hasError = false
        var errMsg: String? = null

        for (provider in providers) {
            try {
                val loc = Location(provider).apply {
                    latitude = lat
                    longitude = lng
                    altitude = _simulationState.value.altitude + Random.nextDouble(-0.5, 0.5)
                    this.bearing = bearing
                    this.speed = speedMps
                    this.accuracy = accuracy
                    time = System.currentTimeMillis()
                    elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        bearingAccuracyDegrees = 4.0f
                        speedAccuracyMetersPerSecond = 0.5f
                        verticalAccuracyMeters = 2.0f
                    }
                }
                locationManager.setTestProviderLocation(provider, loc)
            } catch (se: SecurityException) {
                hasError = true
                errMsg = "Debes activar 'Elegir aplicación para simular ubicación' en Ajustes de desarrollador."
            } catch (e: Exception) {
                hasError = true
                errMsg = e.message
            }
        }

        return Pair(!hasError, errMsg)
    }

    private fun calculateDestinationPoint(
        startLat: Double,
        startLng: Double,
        distanceMeters: Double,
        bearingDeg: Double
    ): Pair<Double, Double> {
        val earthRadius = 6371000.0 // meters
        val dOverR = distanceMeters / earthRadius
        val latRad = Math.toRadians(startLat)
        val lngRad = Math.toRadians(startLng)
        val bearingRad = Math.toRadians(bearingDeg)

        val destLatRad = asin(sin(latRad) * cos(dOverR) + cos(latRad) * sin(dOverR) * cos(bearingRad))
        val destLngRad = lngRad + atan2(
            sin(bearingRad) * sin(dOverR) * cos(latRad),
            cos(dOverR) - sin(latRad) * sin(destLatRad)
        )

        return Pair(Math.toDegrees(destLatRad), Math.toDegrees(destLngRad))
    }

    private fun stopSimulationService() {
        isRunning = false
        simulationJob?.cancel()

        // Clean up test providers
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            try {
                locationManager.setTestProviderEnabled(provider, false)
                locationManager.removeTestProvider(provider)
            } catch (_: Exception) {}
        }

        val currentState = _simulationState.value
        _simulationState.value = currentState.copy(isActive = false)
        preferences.setActive(false)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Protección de Ubicación Activa",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el estado en tiempo real de la ubicación protegida y desplazamiento simulado"
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(city: String, lat: Double, lng: Double, speed: Float): Notification {
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingContentIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, LocationPrivacyService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedLat = String.format("%.4f", lat)
        val formattedLng = String.format("%.4f", lng)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("🛡️ Privacidad GPS Activa • $city")
            .setContentText("Lat: $formattedLat, Lon: $formattedLng • ${String.format("%.1f", speed)} km/h")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingContentIntent)
            .addAction(android.R.drawable.ic_delete, "Detener Protección", pendingStopIntent)
            .build()
    }

    private fun updateNotification() {
        val state = _simulationState.value
        if (!isRunning) return
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(state.currentCity, state.latitude, state.longitude, state.speedKmh))
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
