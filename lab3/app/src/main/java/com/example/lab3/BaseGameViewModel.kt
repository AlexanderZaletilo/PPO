package com.example.lab3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.json.JSONObject


open class BaseGameViewModel: ViewModel() {
    var isHost = true
    protected var id: String? = null
    protected val database = FirebaseDatabase.getInstance().reference
    protected lateinit var matrixRef: DatabaseReference
    protected lateinit var lobbyRef: DatabaseReference
    protected lateinit var clientShotsRef: DatabaseReference
    protected lateinit var hostShotsRef: DatabaseReference
    var yourField: Field? = null
    var enemyField: Field = Field()
    var placedShips = Array<MutableLiveData<Int>>(4)
                        { MutableLiveData<Int>().apply{ value = 0}}
    var hostReady = MutableLiveData<Boolean>().apply{ value = false}
    var clientReady = MutableLiveData<Boolean>().apply{ value = false}
    var started = MutableLiveData<Boolean>().apply{ value = false}
    var isHostTurn = MutableLiveData<Boolean>().apply{value = true}
    var shotListener: onShotListener? = null
    var winner = MutableLiveData<String>().apply{ value = ""}
    interface onShotListener{
        fun onShot(point: Point, isHost: Boolean)
    }
    open fun setUpGame(id: String){
        this.id = id
        matrixRef = database.child("matrix").child(id)
        lobbyRef = database.child(id)
        clientShotsRef = matrixRef.child("clientShots")
        hostShotsRef = matrixRef.child("hostShots")
    }
    fun setReady()
    {
        lobbyRef.child(if(isHost) "host" else "client").child("ready").setValue(true)
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
        }
}