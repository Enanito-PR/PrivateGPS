package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CityLocation
import com.example.model.MovementMode
import com.example.model.SimulationState

class LocationPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gps_privacy_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_ACTIVE = "key_is_active"
        private const val KEY_CITY_NAME = "key_city_name"
        private const val KEY_COUNTRY_NAME = "key_country_name"
        private const val KEY_LATITUDE = "key_latitude"
        private const val KEY_LONGITUDE = "key_longitude"
        private const val KEY_ALTITUDE = "key_altitude"
        private const val KEY_SPEED = "key_speed"
        private const val KEY_BEARING = "key_bearing"
        private const val KEY_MODE = "key_mode"
        private const val KEY_PERSIST_ON_BOOT = "key_persist_on_boot"
        private const val KEY_NATURAL_JITTER = "key_natural_jitter"
    }

    fun saveSimulationState(state: SimulationState) {
        prefs.edit().apply {
            putBoolean(KEY_IS_ACTIVE, state.isActive)
            putString(KEY_CITY_NAME, state.currentCity)
            putString(KEY_COUNTRY_NAME, state.currentCountry)
            putFloat(KEY_LATITUDE, state.latitude.toFloat())
            putFloat(KEY_LONGITUDE, state.longitude.toFloat())
            putFloat(KEY_ALTITUDE, state.altitude.toFloat())
            putFloat(KEY_SPEED, state.speedKmh)
            putFloat(KEY_BEARING, state.bearing)
            putString(KEY_MODE, state.mode.name)
            putBoolean(KEY_PERSIST_ON_BOOT, state.persistOnBoot)
            putBoolean(KEY_NATURAL_JITTER, state.naturalJitterEnabled)
            apply()
        }
    }

    fun loadSimulationState(): SimulationState {
        val isActive = prefs.getBoolean(KEY_IS_ACTIVE, false)
        val city = prefs.getString(KEY_CITY_NAME, "Madrid") ?: "Madrid"
        val country = prefs.getString(KEY_COUNTRY_NAME, "España") ?: "España"
        val lat = prefs.getFloat(KEY_LATITUDE, 40.4168f).toDouble()
        val lng = prefs.getFloat(KEY_LONGITUDE, -3.7038f).toDouble()
        val alt = prefs.getFloat(KEY_ALTITUDE, 650f).toDouble()
        val speed = prefs.getFloat(KEY_SPEED, 4.8f)
        val bearing = prefs.getFloat(KEY_BEARING, 0f)
        val modeStr = prefs.getString(KEY_MODE, MovementMode.WALKING.name)
        val mode = try {
            MovementMode.valueOf(modeStr ?: MovementMode.WALKING.name)
        } catch (_: Exception) {
            MovementMode.WALKING
        }
        val persistOnBoot = prefs.getBoolean(KEY_PERSIST_ON_BOOT, true)
        val naturalJitter = prefs.getBoolean(KEY_NATURAL_JITTER, true)

        return SimulationState(
            isActive = isActive,
            currentCity = city,
            currentCountry = country,
            latitude = lat,
            longitude = lng,
            altitude = alt,
            speedKmh = speed,
            bearing = bearing,
            mode = mode,
            persistOnBoot = persistOnBoot,
            naturalJitterEnabled = naturalJitter
        )
    }

    fun setPersistOnBoot(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PERSIST_ON_BOOT, enabled).apply()
    }

    fun isPersistOnBoot(): Boolean = prefs.getBoolean(KEY_PERSIST_ON_BOOT, true)

    fun setActive(isActive: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ACTIVE, isActive).apply()
    }

    fun isPreviouslyActive(): Boolean = prefs.getBoolean(KEY_IS_ACTIVE, false)
}
