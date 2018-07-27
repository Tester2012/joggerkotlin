package com.example.android.osm.navigation.location

import org.osmdroid.views.overlay.mylocation.IMyLocationProvider

class GpsProviderInfo(val myLocationProvider: IMyLocationProvider?,
                      var currentSpeed: Float = 0f,
                      var totalDistance: Double = 0.0,
                      var totalTime: Long = 0L) {
}