package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.WorldCitiesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Privacidad GPS", appName)
  }

  @Test
  fun `verify world cities repository contains global locations`() {
    val cities = WorldCitiesRepository.defaultCities
    assertTrue(cities.isNotEmpty())
    assertTrue(cities.any { it.name == "Madrid" })
    assertTrue(cities.any { it.name == "Tokio" })
    assertTrue(cities.any { it.name == "Nueva York" })
  }
}
