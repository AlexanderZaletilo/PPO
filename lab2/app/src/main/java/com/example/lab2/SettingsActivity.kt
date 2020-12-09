package com.example.lab2

import android.content.Intent
import android.content.res.TypedArray
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        withActionBar = true
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
        val activity = this
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(activity, MainActivity::class.java))
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(callback)
    }
}