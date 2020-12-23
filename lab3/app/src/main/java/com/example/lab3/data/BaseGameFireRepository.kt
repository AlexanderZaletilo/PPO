package com.example.lab3.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.example.lab3.game.ShotsType
import com.example.lab3.ui.fragments.HomeFragmentDirections
import com.example.lab3.viewmodels.BaseGameViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class BaseGameFireRepository {
    protected var id: String? = null
    protected val auth = Firebase.auth
    protected val database = FirebaseDatabase.getInstance().reference
    protected lateinit var matrixRef: DatabaseReference
    protected lateinit var lobbyRef: DatabaseReference
    protected lateinit var clientShotsRef: DatabaseReference
    protected lateinit var hostShotsRef: DatabaseReference

    fun setUpRefs(id: String)
    {
        this.id = id
        matrixRef = database.child("matrix").child(id)
        lobbyRef = database.child(id)
        clientShotsRef = matrixRef.child("clientShots")
        hostShotsRef = matrixRef.child("hostShots")
    }
    fun setReady(isHost: Boolean)
    {
        lobbyRef.child(if(isHost) "host" else "client").child("ready").setValue(true)
    }

}