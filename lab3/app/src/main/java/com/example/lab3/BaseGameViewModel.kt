package com.example.lab3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.game.Field
import com.example.lab3.game.Ship
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


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
    var clientFieldChanged = MutableLiveData<Boolean>().apply{ value = false}
    var hostFieldChanged = MutableLiveData<Boolean>().apply{value = false}
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
}