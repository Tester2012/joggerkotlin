/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.osm.navigation

import android.content.Context
import com.example.android.osm.navigation.location.GpsLocationSource
import com.example.android.osm.navigation.location.GpsLocationSourceImpl

import com.example.android.osm.navigation.ui.ViewModelFactory

/**
 * Enables injection of data sources.
 */
object Injection {
    private lateinit var gpsLocationSource: GpsLocationSource

    fun registerUserDataSource(gpsLocationSource: GpsLocationSource) {
        this.gpsLocationSource = gpsLocationSource
    }

    fun provideUserDataSource(context: Context): GpsLocationSource {
        if (!this::gpsLocationSource.isInitialized) {
            gpsLocationSource = GpsLocationSourceImpl()
        }
        return gpsLocationSource
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return ViewModelFactory(dataSource.createGpsLocationProvider(context))
    }
}
