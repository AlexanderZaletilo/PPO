package com.example.lab2

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lab2.viewmodels.ListSequencesViewModel
import com.example.lab2.viewmodels.SomeViewModelFactory
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: ListSequencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        withActionBar = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel =
            ViewModelProvider(this, SomeViewModelFactory.getInstance(application)).get(
                ListSequencesViewModel::class.java
            )
        val context = this
        lifecycleScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                viewModel.fetch()
            }
            if(time < 400)
                delay((400 - time).toLong())
        }.invokeOnCompletion {
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        }
    }
}