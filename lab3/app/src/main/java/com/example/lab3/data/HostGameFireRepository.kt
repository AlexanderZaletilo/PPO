package com.example.lab3.data

import com.example.lab3.game.Point
import com.example.lab3.game.ShotsType
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HostGameFireRepository: BaseGameFireRepository() {

    fun setUpGame(id: String,
                  clientConnectedCallback: ((String, String?) -> Unit),
                  shipsSentCallback: ((String) -> Unit)
    ){
        super.setUpRefs(id)
        lobbyRef.child("host").child("ready").setValue(false)
        val clientConnectionListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    clientConnectedCallback(
                        dataSnapshot.child("name").getValue<String>()!!,
                        dataSnapshot.child("imageUrl").value as String
                    )
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        lobbyRef.child("client").addValueEventListener(clientConnectionListener)
        listenPairs.add(
            Pair(lobbyRef.child("client"), clientConnectionListener)
        )
        val shipsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    shipsSentCallback(dataSnapshot.getValue<String>()!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        matrixRef.child("clientShips").addValueEventListener(shipsListener)
        listenPairs.add(
            Pair(matrixRef.child("clientShips"), shipsListener)
        )
    }

    private fun setUpShotMatrices()
    {
        for(i in 0..9)
            for(j in 0..9) {
                clientShotsRef.child(i.toString()).child(j.toString()).setValue(ShotsType.NONE.ordinal)
                hostShotsRef.child(i.toString()).child(j.toString()).setValue(ShotsType.NONE.ordinal)
            }
    }
    fun applyShotChanges(point: Point, isHost: Boolean, type: ShotsType)
    {
        (if(isHost) hostShotsRef else clientShotsRef)
            .child(point.row.toString()).child(point.col.toString()).setValue(type.ordinal)
    }
    fun setCurrentTurn(value: Boolean)
    {
        lobbyRef.child("isHostTurn").setValue(value)
    }
    fun setWinner(value: String){
        lobbyRef.child("winner").setValue(value)
        GlobalScope.launch(Dispatchers.IO) {
            clear()
        }
    }
    override fun clear()
    {
        super.clear()
        Thread.sleep(10000)
        matrixRef.removeValue()
        lobbyRef.removeValue()
    }
    fun onGameStarted(clientShotCallback: ((String) -> Unit))
    {
        setUpShotMatrices()
        lobbyRef.child("isHostTurn").setValue(true)
        lobbyRef.child("started").setValue(true)
        val clientShotListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    clientShotCallback(dataSnapshot.getValue<String>()!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        matrixRef.child("clientShot")
            .addValueEventListener(clientShotListener)
        listenPairs.add(
            Pair(matrixRef.child("clientShot"), clientShotListener)
        )
    }
    companion object {
        private var instance: HostGameFireRepository? = null
        fun getInstance(): HostGameFireRepository{
            if(instance == null)
                instance = HostGameFireRepository()
            return instance!!
        }
    }
}