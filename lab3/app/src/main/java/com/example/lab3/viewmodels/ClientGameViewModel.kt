package com.example.lab3.viewmodels

import com.example.lab3.game.ShotsType
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


class ClientGameViewModel: BaseGameViewModel() {

    override fun setUpGame(id: String){
        if(this.id == null)
        {
            super.setUpGame(id)
            val hostReadyListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists())
                        hostReady.value = true
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            database.child(id).child("host").child("ready")
                .addValueEventListener(hostReadyListener)
            val startedListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue<Boolean>()!!)
                        started.value = true
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            lobbyRef.child("started").addValueEventListener(startedListener)
            val winnerListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists() && dataSnapshot.getValue<String>()!! != "")
                        winner.value = dataSnapshot.getValue<String>()!!
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            lobbyRef.child("winner").addValueEventListener(winnerListener)
            enemyField = Field()
        }
    }
    fun sendShipsToHost()
    {
        matrixRef.child("clientShips")
            .setValue(Ship.ArrayToJsonString(yourField!!.ships.toTypedArray()))
    }
    fun sendTurnToHost(point: Point)
    {
        matrixRef.child("clientShot").setValue(Point.toJsonString(point))
    }
    fun onGameStarted(){
        val hostTurnListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                    isHostTurn.value = dataSnapshot.getValue<Boolean>()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        lobbyRef.child("isHostTurn").addValueEventListener(hostTurnListener)
        for(i in 0..9)
            for(j in 0..9)
            {
                val cellHostShotsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        yourField!![i, j].status = ShotsType.values()[dataSnapshot.getValue<Int>()!!]
                        shotListener?.onShot(Point(i, j), true)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                hostShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellHostShotsListener)
                val cellClientShotsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        enemyField!![i, j].status = ShotsType.values()[dataSnapshot.getValue<Int>()!!]
                        shotListener?.onShot(Point(i, j), false)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                clientShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellClientShotsListener)
            }
    }
}