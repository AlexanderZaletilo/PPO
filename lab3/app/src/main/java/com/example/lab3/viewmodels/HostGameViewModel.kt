package com.example.lab3.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.lab3.game.ShotsType
import com.example.lab3.game.Cell
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class HostGameViewModel: BaseGameViewModel() {
    var clientField: Field? = null
    var hostDestroyedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    private var clientDestroyedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    var clientConnected =  MutableLiveData<Boolean>().apply{ value = false}
    override fun setUpGame(id: String){
        if(this.id == null) {
            super.setUpGame(id)
            lobbyRef.child("host").child("ready").setValue(false)
            val clientConnectionListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        val name = dataSnapshot.child("name").getValue<String>()
                        enemyName.value = name ?: "Anonymous"
                        GlobalScope.launch(Dispatchers.IO) {
                            downloadImage(dataSnapshot.child("imageUrl").value as String)
                        }
                        clientConnected.value = true
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            lobbyRef.child("client").addValueEventListener(clientConnectionListener)
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
        for(i in 0..9)
            for(j in 0..9) {
                clientShotsRef.child(i.toString()).child(j.toString()).setValue(ShotsType.NONE.ordinal)
                hostShotsRef.child(i.toString()).child(j.toString()).setValue(ShotsType.NONE.ordinal)
            }
    }
    fun onGameStarted(){
        setUpShotMatrices()
        lobbyRef.child("isHostTurn").setValue(true)
        lobbyRef.child("started").setValue(true)
        val clientShotListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists() && !isHostTurn.value!!) {
                    val point = Point.fromJsonObject(JSONObject(dataSnapshot.getValue<String>()!!))
                    processShot(point, false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        matrixRef.child("clientShot")
                .addValueEventListener(clientShotListener)
    }
    fun processShot(point: Point, isHost: Boolean)
    {
        val cell: Cell
        val checkCell: Cell
        val shotsRef: DatabaseReference
        val checkField: Field
        val field: Field
        if(isHost)
        {
            field = enemyField
            cell = enemyField[point]
            checkField = clientField!!
            checkCell = clientField!![point]
            shotsRef = hostShotsRef
        }
        else
        {
            field = yourField!!
            cell = yourField!![point]
            checkField = yourField!!
            checkCell = cell
            shotsRef = clientShotsRef
        }
        if(cell.status != ShotsType.NONE)
            return
        if(checkCell.ship == null)
            cell.status = ShotsType.MISSED
        else {
            cell.status = ShotsType.HIT
            processShotCell(checkField, field, checkCell, shotsRef, isHost)
        }
        applyShotChanges(point, shotsRef, cell.status)
        if(cell.status != ShotsType.HIT) {
            isHostTurn.value = isHostTurn.value!! xor true
            lobbyRef.child("isHostTurn").setValue(isHostTurn.value!!)
        }
    }
    private fun applyShotChanges(point: Point, shotsRef: DatabaseReference, type: ShotsType)
    {
        shotListener?.onShot(point, isHostTurn.value!!)
        shotsRef.child(point.row.toString()).child(point.col.toString()).setValue(type.ordinal)
    }
    private fun processShotCell(checkField: Field, field: Field, cell: Cell, shotsRef: DatabaseReference, isHost: Boolean)
    {
        cell.ship!!.isBrokenParts[cell.part] = true
        if(cell.ship!!.isBroken())
        {
            val points = checkField.getSurroundCells(cell.ship!!)
            field.markCellsAt(points)
            for(point in points)
                applyShotChanges(point, shotsRef, ShotsType.MISSED)
            val destroyedShips = if(isHost) clientDestroyedShips else hostDestroyedShips
            destroyedShips[cell.ship!!.length - 1].value = destroyedShips[cell.ship!!.length - 1].value!! + 1
            if(isAllShipsDestroyed(destroyedShips))
            {
                winner.value = if(isHost) "host" else "client"
                lobbyRef.child("winner").setValue(winner.value)
                GlobalScope.launch(Dispatchers.IO) {
                    Thread.sleep(10000)
                    matrixRef.removeValue()
                    lobbyRef.removeValue()
                }
            }
        }
    }
    private fun isAllShipsDestroyed(shipsCounter: Array<MutableLiveData<Int>>): Boolean
    {
        for((i, live) in shipsCounter.withIndex())
            if(live.value!! < 4 - i)
                return false
        return true
    }
    override fun clear() {
        super.clear()
        clientConnected.value = false
        clientField = null
        for(i in 0..3)
        {
            hostDestroyedShips[i].value = 0
            clientDestroyedShips[i].value = 0
        }
    }
}