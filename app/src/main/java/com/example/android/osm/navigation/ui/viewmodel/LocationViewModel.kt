package com.example.android.osm.navigation.ui.viewmodel

import android.arch.lifecycle.ViewModel
import com.example.android.osm.navigation.location.GpsLocationProvider
import com.example.android.osm.navigation.location.GpsProviderLiveData

class LocationViewModel(val gpsLocationProvider: GpsLocationProvider) : ViewModel() {
    lateinit var gpsProviderLiveData: GpsProviderLiveData

    fun getGpsProvider(): GpsProviderLiveData {
        if (!this::gpsProviderLiveData.isInitialized) {
            gpsProviderLiveData = GpsProviderLiveData(gpsLocationProvider)
        }
        return gpsProviderLiveData
    }
}