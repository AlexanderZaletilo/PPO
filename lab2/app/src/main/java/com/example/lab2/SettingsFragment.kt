package com.example.lab2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var backColor: Int? = null
    private lateinit var settingsActivity: SettingsActivity
    @SuppressLint("ResourceType")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingsActivity = context as SettingsActivity
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val attrs = settingsActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.colorPrimary, R.attr.colorSecond))
        (context as BaseActivity).supportActionBar!!
            .setBackgroundDrawable(
                ColorDrawable( attrs.getColor(0, Color.RED)))
        backColor = attrs.getColor(1, Color.WHITE)
        if((context as BaseActivity).isDark!!)
            settingsActivity.setTheme(R.style.SettingsNightStyle)
        else
            settingsActivity.setTheme(R.style.SettingsLightStyle)
        val view =  super.onCreateView(inflater, container, savedInstanceState)
        view!!.setBackgroundColor(backColor!!);
        return view
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key != "font")
            requireActivity().recreate()
    }
    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}

