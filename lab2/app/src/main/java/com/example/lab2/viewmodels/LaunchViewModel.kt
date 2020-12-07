package com.example.lab2.viewmodels

import androidx.lifecycle.*
import com.example.lab2.LaunchService

class LaunchViewModel: ViewModel() {
    public var service: LaunchService? = null
    public var isBound: Boolean = false
}