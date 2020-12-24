package com.example.lab3.data

import android.widget.TextView
import androidx.navigation.findNavController
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.example.lab3.ui.fragments.HomeFragmentDirections
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class ClientGameFireRepository: BaseGameFireRepository() {

    fun setUpGame(id: String,
                  hostDataCallback: ((String, String?) -> Unit),
                  hostReadyCallback: (() -> Unit),
                  startedCallback: (() -> Unit),
                  gotWinnerCallback: ((String) -> Unit)) {
        super.setUpRefs(id)
        val hostDataListener = object : BaseValueListener() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hostDataCallback(dataSnapshot.child("name").getValue<String>() ?: "Anonymous",
                                 dataSnapshot.child("imageUrl").value as String
                )
            }
        }
        listenPairs.add(
            Pair(lobbyRef.child("host"), hostDataListener)
        )
        lobbyRef.child("host").addListenerForSingleValueEvent(hostDataListener)
        val hostReadyListener = object : BaseValueListener() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                   hostReadyCallback()
            }
        }
        listenPairs.add(
            Pair(lobbyRef.child("host").child("ready"), hostReadyListener)
        )
        lobbyRef.child("host").child("ready")
            .addValueEventListener(hostReadyListener)
        val startedListener = object : BaseValueListener() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue<Boolean>()!!)
                    startedCallback()
            }
        }
        listenPairs.add(
            Pair(lobbyRef.child("started").child("ready"), startedListener)
        )
        lobbyRef.child("started").addValueEventListener(startedListener)
        val winnerListener = object : BaseValueListener() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val value = dataSnapshot.getValue<String>()
                    if (value != null && value != "")
                        gotWinnerCallback(value)
                }
            }
        }
        listenPairs.add(
            Pair(lobbyRef.child("winner").child("ready"), winnerListener)
        )
        lobbyRef.child("winner").addValueEventListener(winnerListener)
    }

    fun onGameStarted(hostTurnCallback: ((Boolean) -> Unit),
                      hostShotCallback: ((Int, Int, Int) -> Unit),
                      clientShotCallback: ((Int, Int, Int) -> Unit))
    {
        val hostTurnListener = object : BaseValueListener() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                {
                    val value = dataSnapshot.getValue<Boolean>()
                    if(value != null)
                        hostTurnCallback(value)
                }
            }
        }
        lobbyRef.child("isHostTurn").addValueEventListener(hostTurnListener)
        listenPairs.add(
            Pair(lobbyRef.child("isHostTurn"), hostTurnListener)
        )
        for(i in 0..9)
            for(j in 0..9)
            {
                val cellHostShotsListener = object : BaseValueListener() {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value = dataSnapshot.getValue<Int>()
                        if(value != null)
                            hostShotCallback(i, j, dataSnapshot.getValue<Int>()!!)
                    }
                }
                hostShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellHostShotsListener)
                listenPairs.add(
                    Pair(hostShotsRef.child(i.toString()).child(j.toString()), cellHostShotsListener)
                )
                val cellClientShotsListener = object : BaseValueListener() {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value = dataSnapshot.getValue<Int>()
                        if(value != null)
                            clientShotCallback(i, j, value)
                    }
                }
                clientShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellClientShotsListener)
                listenPairs.add(
                    Pair(clientShotsRef.child(i.toString()).child(j.toString()), cellHostShotsListener)
                )
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
    fun tryJoin(id: String, joinCallback: ((Boolean) -> Unit)) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists() && !dataSnapshot.child("client").exists()) {
                    database.child(id!!).child("client")
                            .setValue(mapOf("name" to user!!.displayName,
                                    "imageUrl" to user!!.providerData[0].photoUrl.toString()))
                    joinCallback(true)
                }
                else {
                    joinCallback(false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                joinCallback(false)
            }
        }
        database.child(id).addListenerForSingleValueEvent(listener)
    }
}