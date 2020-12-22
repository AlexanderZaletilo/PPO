package com.example.lab3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.game.Field
import com.example.lab3.game.Ship
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


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
            enemyField = Field()
        }
    }
    fun sendShipsToHost()
    {
        matrixRef.child("clientShips")
            .setValue(Ship.ArrayToJsonString(yourField!!.ships.toTypedArray()))
    }
    fun onGameStarted(){
        for(i in 0..9)
            for(j in 0..9)
            {
                val cellHostShotsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        yourField!![i, j].status = ShotsType.values()[dataSnapshot.getValue<Int>()!!]
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                hostShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellHostShotsListener)
                val cellClientShotsListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        enemyField!![i, j].status = ShotsType.values()[dataSnapshot.getValue<Int>()!!]
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                clientShotsRef.child(i.toString()).child(j.toString())
                    .addValueEventListener(cellClientShotsListener)
            }
    }
}