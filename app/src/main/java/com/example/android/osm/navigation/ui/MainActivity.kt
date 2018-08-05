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

package com.example.android.osm.navigation.ui

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.android.osm.jogger.BuildConfig
import com.example.android.osm.jogger.R
import com.example.android.osm.navigation.Injection
import com.example.android.osm.navigation.location.GpsProviderInfo
import com.example.android.osm.navigation.location.GpsProviderLiveData
import com.example.android.osm.navigation.ui.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToLong


/**
 * Main screen of the app. Displays a user name and gives the option to update the user name.
 */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var myLocationNewOverlay: MyLocationNewOverlay

    private lateinit var locationLiveData: GpsProviderLiveData

    private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET)
    private val PERMISSIONS_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (hasPermissions()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.download_area -> {
                val cacheManager = CacheManager(mapView)
                cacheManager.downloadAreaAsync(this, mapView.boundingBox,
                        mapView.zoomLevelDouble.toInt(), 17)
                return true
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap()
                }
            }
        }
    }

    private fun hasPermissions():Boolean {
        PERMISSIONS.iterator().forEach {
            if (ContextCompat.checkSelfPermission(this, it)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private fun initMap() {
        setContentView(R.layout.activity_main)
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.zoomTo(17, null)

        val modelFactory = Injection.provideViewModelFactory(applicationContext)
        val locationViewModel = ViewModelProviders.of(this, modelFactory).get(LocationViewModel::class.java)
        locationLiveData = locationViewModel.getGpsProvider()
        locationLiveData.observe(this, Observer<GpsProviderInfo> { it ->
            it?.let {
                if (!this::myLocationNewOverlay.isInitialized) {
                    myLocationNewOverlay = MyLocationNewOverlay(it.myLocationProvider, mapView)
                    mapView.overlayManager.add(myLocationNewOverlay)
                    myLocationNewOverlay.enableMyLocation()
                } else {
                    val geoPoint = GeoPoint(it.myLocationProvider.lastKnownLocation)
                    mapView.controller.setCenter(geoPoint)
                    val speed = "%.1f".format(it.currentSpeed)
                    val distance = it.totalDistance.roundToLong()
                    val time = it.totalTime / 1000
                    total_time.text = "$time secs"
                    current_speed.text = "$speed m/s"
                    total_distance.text = "$distance m"
                }
            }
        })
    }
}
