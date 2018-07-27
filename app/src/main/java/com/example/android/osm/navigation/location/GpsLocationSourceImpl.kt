package com.example.android.osm.navigation.location

import android.content.Context

class GpsLocationSourceImpl: GpsLocationSource {
    override fun createGpsLocationProvider(context: Context): GpsLocationProvider {
        return GpsLocationProvider(context)
    }
}