package com.example.android.osm.navigation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.android.osm.jogger.R

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var settingsFragment: Fragment? = supportFragmentManager.findFragmentByTag(SettingsFragment.TAG)
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.main_content,
                settingsFragment, SettingsFragment.TAG)
                .commit()
    }
}