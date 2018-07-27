package com.example.android.osm.navigation.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.example.android.osm.jogger.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity



class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        val TAG =  SettingsFragment::class.java.name
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_settings)

        findPreference("pref_key_license").setOnPreferenceClickListener {
            val intent = Intent(activity, OssLicensesMenuActivity::class.java)
            startActivity(intent)
             true
        }
    }
}