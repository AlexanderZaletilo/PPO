package com.example.lab3.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.lab3.data.HostGameFireRepository
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
    private var hostRepos = HostGameFireRepository.getInstance()
    var clientField: Field? = null
    var hostDestroyedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    private var clientDestroyedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    var clientConnected =  MutableLiveData<Boolean>().apply{ value = false}
    init {
        repos = hostRepos
        onError = repos.onError
    }

    override fun setUpGame(id: String){
        super.setUpGame(id)
        val clientConnectedCallback = { name: String, imageUrl: String? ->
            enemyName.value = name
            GlobalScope.launch(Dispatchers.IO) {
                downloadImage(imageUrl!!)
            }
            clientConnected.value = true
        }
        val shipsSentCallback = {ships: String ->
            clientField = Field()
            clientField!!.importShips(Ship.fromArrayJsonString(ships))
            clientReady.value = true
        }
        hostRepos.setUpGame(id, clientConnectedCallback, shipsSentCallback)
    }
    fun onGameStarted(){
        hostRepos.onGameStarted {
            if(!isHostTurn.value!!) {
                val point = Point.fromJsonObject(JSONObject(it))
                processShot(point, false)
            }
        }
    }
    fun processShot(point: Point, isHost: Boolean)
    {
        val cell: Cell
        val checkCell: Cell
        val checkField: Field
        val field: Field
        if(isHost)
        {
            field = enemyField
            cell = enemyField[point]
            checkField = clientField!!
            checkCell = clientField!![point]
        }
        else
        {
            field = yourField!!
            cell = yourField!![point]
            checkField = yourField!!
            checkCell = cell
        }
        if(cell.status != ShotsType.NONE)
            return
        if(checkCell.ship == null)
            cell.status = ShotsType.MISSED
        else {
            cell.status = ShotsType.HIT
            processShotCell(checkField, field, checkCell, isHost)
        }
        applyShotChanges(point, isHost, cell.status)
        if(cell.status != ShotsType.HIT) {
            isHostTurn.value = isHostTurn.value!! xor true
            hostRepos.setCurrentTurn(isHostTurn.value!!)
        }
    }
    private fun applyShotChanges(point: Point, isHost: Boolean, type: ShotsType)
    {
        shotListener?.onShot(point, isHostTurn.value!!)
        hostRepos.applyShotChanges(point, isHost, type)
    }
    private fun processShotCell(checkField: Field, field: Field, cell: Cell, isHost: Boolean)
    {
        cell.ship!!.isBrokenParts[cell.part] = true
        if(cell.ship!!.isBroken())
        {
            val points = checkField.getSurroundCells(cell.ship!!)
            field.markCellsAt(points)
            for(point in points)
                applyShotChanges(point, isHost, ShotsType.MISSED)
            val destroyedShips = if(isHost) clientDestroyedShips else hostDestroyedShips
            destroyedShips[cell.ship!!.length - 1].value = destroyedShips[cell.ship!!.length - 1].value!! + 1
            if(isAllShipsDestroyed(destroyedShips))
            {
                winner.value = if(isHost) "host" else "client"
                hostRepos.setWinner(winner.value!!)
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