package com.example.lab2

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        withActionBar = true
        super.onCreate(savedInstanceState)
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val fontId = when(preference.getString("font", "medium"))
        {
            "small" -> R.style.FontStyle_Small
            "large" -> R.style.FontStyle_Large
            else -> R.style.FontStyle_Medium
        }
        this.theme?.applyStyle(fontId!!, true);
        setContentView(R.layout.activity_main)
        val navController: NavController = Navigation.findNavController(
            this,
            R.id.my_nav_host_fragment
        );
        NavigationUI.setupActionBarWithNavController(this, navController);
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.my_nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true // must return true to consume it here

        }
        return super.onOptionsItemSelected(item)
    }
}