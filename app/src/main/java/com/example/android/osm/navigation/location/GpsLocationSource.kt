package com.example.android.osm.navigation.location

import android.content.Context

interface GpsLocationSource {
    fun createGpsLocationProvider(context: Context): GpsLocationProvider
}