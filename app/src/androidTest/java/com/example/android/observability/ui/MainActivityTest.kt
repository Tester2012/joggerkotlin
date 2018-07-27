package com.example.android.observability.ui

import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.example.android.osm.navigation.Injection
import com.example.android.osm.navigation.location.GpsLocationProvider
import com.example.android.osm.navigation.location.GpsLocationSource
import com.example.android.osm.navigation.ui.MainActivity
import junit.framework.Assert
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.math.roundToLong

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var locationManager: LocationManager
    private val LOCATION_SOURCE = "gps_mock"

    @Rule
    @JvmField
    val activityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
        override fun beforeActivityLaunched() {
            val mockProvider = GpsLocationProvider(getTargetContext())
            mockProvider.clearLocationSources()
            mockProvider.addLocationSource(LOCATION_SOURCE)
            val gpsLocationSource = mock(GpsLocationSource::class.java)
            doReturn(mockProvider).`when`(gpsLocationSource).createGpsLocationProvider(
                    anyObject())
            Injection.registerUserDataSource(gpsLocationSource)

            locationManager = getTargetContext().getSystemService(LOCATION_SERVICE)
                    as LocationManager
            if (locationManager.getProvider(LOCATION_SOURCE) != null) {
                locationManager.removeTestProvider(LOCATION_SOURCE)
            }
            locationManager.addTestProvider(LOCATION_SOURCE, false, false, false,
                    false, false, true, true,
                    0, 0)
            locationManager.setTestProviderEnabled(LOCATION_SOURCE, true)
        }
    }

    @Test
    fun test1() {
        val startPoint = initLocation(52.520008, 13.404954)
        var currentPoint = Location(startPoint)
        val LAST = 20
        // feed 10 points, one per second
        for (i in 0..LAST) {
            locationManager.setTestProviderLocation(LOCATION_SOURCE, currentPoint)
            SystemClock.sleep(1000)
            currentPoint = initLocation(currentPoint.latitude, currentPoint.longitude)
            if (i < LAST) {
                currentPoint.latitude += 0.0001
            }
        }
        val expectedDistance = startPoint.distanceTo(currentPoint).roundToLong()
        val actualDistance = activityTestRule.activity.total_distance.text
        Assert.assertEquals("$expectedDistance m", actualDistance)
    }

    private fun initLocation(latitude: Double, longitude: Double): Location {
        val location = Location(LOCATION_SOURCE)
        location.latitude = latitude
        location.longitude = longitude
        location.accuracy = 0f
        location.time = SystemClock.currentThreadTimeMillis()
        location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

        return location
    }

    private fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }
}