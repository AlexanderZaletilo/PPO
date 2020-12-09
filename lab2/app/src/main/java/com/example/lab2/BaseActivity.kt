package com.example.lab2

import android.content.res.TypedArray
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*

open class BaseActivity : AppCompatActivity() {
    public var isDark: Boolean? = null
    protected var withActionBar: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val locale = Locale(preference.getString("lang", "en"))
        val config = resources.configuration
        val appConfig = application.resources.configuration
        if(locale.language != config.locale.language) {
            appConfig.locale = locale
            config.locale = locale;
            application.resources.updateConfiguration(appConfig, application.resources.displayMetrics)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
        isDark = preference.getBoolean("isNight", false)
        if(withActionBar!!)
            this.setTheme(if(isDark!!) R.style.DarkTheme else R.style.AppTheme)
        else
            this.setTheme(if(isDark!!) R.style.DarkTheme_NoActionBar else R.style.AppTheme_NoActionBar)
    }
}