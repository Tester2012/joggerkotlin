package com.example.android.osm.navigation.location

import android.content.Context
import android.location.Location
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer

class GpsLocationProvider(context: Context): GpsMyLocationProvider(context) {
    private var locationListener: IMyLocationConsumer? = null

    override fun onLocationChanged(location: Location?) {
        super.onLocationChanged(location)

        locationListener?.onLocationChanged(location, this)
    }

    fun setLocationListener(locationListener: IMyLocationConsumer?) {
        this.locationListener = locationListener
    }
}