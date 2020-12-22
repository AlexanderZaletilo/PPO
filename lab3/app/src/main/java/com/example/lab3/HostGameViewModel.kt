package com.example.lab3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.game.Field
import com.example.lab3.game.Ship
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class HostGameViewModel: BaseGameViewModel() {
    var clientField: Field? = null
    private var clientShotsField: Field? = null
    private var hostShotsField: Field? = null
    var clientConnected =  MutableLiveData<Boolean>().apply{ value = false}
    override fun setUpGame(id: String){
        if(this.id == null) {
            super.setUpGame(id)
            lobbyRef.child("host").child("ready").setValue(false)
            val clientConnectionListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists())
                        clientConnected.value = true
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            lobbyRef.child("client").child("email")
                .addValueEventListener(clientConnectionListener)
            val shipsListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        clientField = Field()
                        val string = dataSnapshot.getValue<String>()
                        clientField!!.importShips(Ship.fromArrayJsonString(string!!))
                        clientReady.value = true
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            matrixRef.child("clientShips")
                .addValueEventListener(shipsListener)
        }
    }
    private fun setUpShotMatrices()
    {
        clientShotsField = Field()
        hostShotsField = Field()
        for(i in 0..9)
            for(j in 0..9) {
                clientShotsRef.child(i.toString()).child(j.toString()).setValue(ShotsType.NONE.ordinal)
                hostShotsRef.child(i.toString()).child(j.toString()).setValue(ShotsType.NONE.ordinal)
            }
    }
    fun onGameStarted(){
        setUpShotMatrices()
        lobbyRef.child("started").setValue(true)
    }
}