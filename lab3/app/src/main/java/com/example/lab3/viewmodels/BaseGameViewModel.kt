package com.example.lab3.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.data.BaseGameFireRepository
import com.example.lab3.data.StatsRepository
import com.example.lab3.game.Field
import com.example.lab3.game.GameStats
import com.example.lab3.game.Point
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream


open class BaseGameViewModel(val context: Application): AndroidViewModel(context) {
    var isHost = true
    protected lateinit var repos: BaseGameFireRepository
    protected var statsRepos = StatsRepository(context)
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
    lateinit var onError: MutableLiveData<Boolean>
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
        enemyField = Field()
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
        if(!repos.clearedRefs)
            GlobalScope.launch(Dispatchers.IO) { repos.clear() }
    }
    fun insertStats(stats: GameStats)
    {
        GlobalScope.launch(Dispatchers.IO){
            statsRepos.insertStats(stats)
        }
    }
    fun downloadImage(path: String)
    {
        val inStream = java.net.URL(path).openStream()
        enemyImage.postValue(BitmapFactory.decodeStream(inStream))
        inStream.close()
    }
}