package com.example.android.osm.navigation.location

import android.arch.lifecycle.LiveData
import android.location.Location
import android.util.Log
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer

class GpsProviderLiveData(val gpsLocationProvider: GpsLocationProvider): LiveData<GpsProviderInfo>() {
    private lateinit var prevLocation: Location
    private var totalDistance = 0.0
    private var initialTime = 0L

    private val LOG_TAG = GpsProviderLiveData::class.java.name
    private val WORST_ACCURACY = 10f

    private val locationListener = IMyLocationConsumer { location, source ->
        val currentTimeMillis = System.currentTimeMillis()
        val gpsProviderInfo = GpsProviderInfo(source)
        if (initialTime <= 0) {
            initialTime = currentTimeMillis
            value = gpsProviderInfo
        }
        location?.let {
            Log.d(LOG_TAG, "Incoming accuracy : " + location.accuracy)
            if (location.accuracy < WORST_ACCURACY) {
                if (!::prevLocation.isInitialized) {
                    prevLocation = location
                }

                totalDistance += location.distanceTo(prevLocation)
                gpsProviderInfo.currentSpeed = location.speed
                gpsProviderInfo.totalDistance = totalDistance
                gpsProviderInfo.totalTime = (currentTimeMillis - initialTime)
                prevLocation = Location(location)
            }

            value = gpsProviderInfo
        }
    }

    init {
        //receive updates every second even if the device is not moving
        gpsLocationProvider.locationUpdateMinDistance = 0f
        gpsLocationProvider.locationUpdateMinTime = 1000

        gpsLocationProvider.setLocationListener(locationListener)
    }

    override fun onActive() {
        locationListener.onLocationChanged(gpsLocationProvider.lastKnownLocation,
                gpsLocationProvider)
    }
}