package com.example.lab3.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
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

class ClientGameFireRepository: BaseGameFireRepository() {

    fun setUpGame(id: String,
                  hostDataCallback: ((String, String?) -> Unit),
                  hostReadyCallback: (() -> Unit),
                  startedCallback: (() -> Unit),
                  gotWinnerCallback: ((String) -> Unit)) {
        super.setUpRefs(id)
        val hostDataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hostDataCallback(dataSnapshot.child("name").getValue<String>() ?: "Anonymous",
                                 dataSnapshot.child("imageUrl").value as String
                )
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        lobbyRef.child("host").addListenerForSingleValueEvent(hostDataListener)
        val hostReadyListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                   hostReadyCallback()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        database.child(id).child("host").child("ready")
            .addValueEventListener(hostReadyListener)
        val startedListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue<Boolean>()!!)
                    startedCallback()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        lobbyRef.child("started").addValueEventListener(startedListener)
        val winnerListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue<String>()!! != "")
                    gotWinnerCallback(dataSnapshot.getValue<String>()!!)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        lobbyRef.child("winner").addValueEventListener(winnerListener)
    }

    fun onGameStarted(hostTurnCallback: ((Boolean) -> Unit),
                      hostShotCallback: ((Int, Int, Int) -> Unit),
                      clientShotCallback: ((Int, Int, Int) -> Unit))
    {
        val hostTurnListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                    hostTurnCallback(dataSnapshot.getValue<Boolean>()!!)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        lobbyRef.child("isHostTurn").addValueEventListener(hostTurnListener)
        for(i in 0..9)
            for(j in 0..9)
            {
                val cellHostShotsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        hostShotCallback(i, j, dataSnapshot.getValue<Int>()!!)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                hostShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellHostShotsListener)
                val cellClientShotsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        clientShotCallback(i, j, dataSnapshot.getValue<Int>()!!)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                clientShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellClientShotsListener)
            }
    }

    fun sendShipsToHost(ships: Array<Ship>)
    {
        matrixRef.child("clientShips").setValue(Ship.ArrayToJsonString(ships))
    }
    fun sendTurnToHost(point: Point)
    {
        matrixRef.child("clientShot").setValue(Point.toJsonString(point))
    }
    companion object {
        private var instance: ClientGameFireRepository? = null
        fun getInstance(): ClientGameFireRepository{
            if(instance == null)
                instance = ClientGameFireRepository()
            return instance!!
        }
    }
}