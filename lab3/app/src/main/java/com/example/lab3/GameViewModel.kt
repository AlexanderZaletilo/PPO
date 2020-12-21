package com.example.lab3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.game.Field

class GameViewModel: ViewModel() {
    var field: Field? = null
    var placedShips = arrayOf(MutableLiveData<Int>().apply{ value = 0},
            MutableLiveData<Int>().apply{ value = 0},
            MutableLiveData<Int>().apply{ value = 0},
            MutableLiveData<Int>().apply{ value = 0})
    var hostConnected =  MutableLiveData<Int>().apply{ value = 0}
    var serverConnected =  MutableLiveData<Int>().apply{ value = 0}
    fun isAllPlaced(): Boolean
    {
        for(i in 0 .. 3)
            if(placedShips[3 - i].value != i  + 1)
                return false
        return true
    }
}