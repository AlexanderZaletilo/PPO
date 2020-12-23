package com.example.lab3.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.data.BaseGameFireRepository
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayInputStream


open class BaseGameViewModel: ViewModel() {
    var isHost = true
    protected lateinit var repos: BaseGameFireRepository
    var yourField: Field? = null
    var enemyField: Field = Field()
    var placedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    var hostReady = MutableLiveData<Boolean>().apply{ value = false}
    var clientReady = MutableLiveData<Boolean>().apply{ value = false}
    var started = MutableLiveData<Boolean>().apply{ value = false}
    var isHostTurn = MutableLiveData<Boolean>().apply{value = true}
    var shotListener: onShotListener? = null
    var winner = MutableLiveData<String>().apply{ value = ""}
    var enemyName = MutableLiveData<String>()
    var enemyImage = MutableLiveData<Bitmap>()
    interface onShotListener{
        fun onShot(point: Point, isHost: Boolean)
    }
    open fun setUpGame(id: String){
        repos.setUpRefs(id)
    }
    fun setReady()
    {
        repos.setReady(isHost)
    }
    fun isAllPlaced(): Boolean
    {
        for(i in 0 .. 3)
            if(placedShips[3 - i].value != i  + 1)
                return false
        return true
    }
    open fun clear()
    {
        yourField = null
        shotListener = null
        hostReady.value = false
        clientReady.value = false
        started.value = false
        isHostTurn.value = true
        winner.value = ""
        placedShips = Array(4)
        { MutableLiveData<Int>().apply{ value = 0}}
        enemyImage.value = null
        enemyName.value = null
    }
    fun downloadImage(path: String)
    {
        val inStream = java.net.URL(path).openStream()
        enemyImage.postValue(BitmapFactory.decodeStream(inStream))
        inStream.close()
    }
}