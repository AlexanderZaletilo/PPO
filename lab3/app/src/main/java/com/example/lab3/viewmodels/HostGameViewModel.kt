package com.example.lab3.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab3.data.HostGameFireRepository
import com.example.lab3.game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class HostViewModelFactory(private val context: Application): ViewModelProvider.NewInstanceFactory() {
    companion object {
        var instance: HostViewModelFactory? = null
        var viewModelInstance: HostGameViewModel? = null
        fun getInstance(context: Application): HostViewModelFactory{
            if(instance == null)
                instance = HostViewModelFactory(context)
            return instance!!
        }
    }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if(viewModelInstance == null)
            viewModelInstance = HostGameViewModel(context)
        return viewModelInstance as T
    }
}

class HostGameViewModel(context: Application): BaseGameViewModel(context) {
    private var hostRepos = HostGameFireRepository.getInstance()
    var clientField: Field? = null
    var hostDestroyedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    private var clientDestroyedShips = Array(4)
    { MutableLiveData<Int>().apply{ value = 0}}
    var clientConnected =  MutableLiveData<Boolean>().apply{ value = false}
    private var hostStats: GameStats? = null
    private var clientStats: GameStats? = null
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
        hostStats = GameStats(enemyName.value ?: "Anonymous", Date())
        clientStats = GameStats(repos.user!!.displayName ?: "Anonymous", Date())
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
        val stats: GameStats
        if(isHost)
        {
            field = enemyField
            cell = enemyField[point]
            checkField = clientField!!
            checkCell = clientField!![point]
            stats = hostStats!!
        }
        else
        {
            field = yourField!!
            cell = yourField!![point]
            checkField = yourField!!
            checkCell = cell
            stats = clientStats!!
        }
        if(cell.status != ShotsType.NONE)
            return
        if(checkCell.ship == null) {
            cell.status = ShotsType.MISSED
        }
        else {
            cell.status = ShotsType.HIT
            stats.hits += 1
            processShotCell(checkField, field, checkCell, isHost)
        }
        stats.shots += 1
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
                completeAndSendStats(isHost)
                winner.value = if(isHost) "host" else "client"
                hostRepos.setWinner(winner.value!!)
            }
        }
    }
    private fun completeAndSendStats(isHostWinner: Boolean)
    {
        hostStats!!.shipsDestroyedCount = clientDestroyedShips.map{ it.value!!}.toTypedArray()
        clientStats!!.shipsDestroyedCount = hostDestroyedShips.map{ it.value!!}.toTypedArray()
        hostStats!!.isWin = isHostWinner
        clientStats!!.isWin = !isHostWinner
        hostStats!!.ended = Date()
        clientStats!!.ended = Date()
        hostRepos.sendStats(clientStats!!)
        GlobalScope.launch(Dispatchers.IO) {
            statsRepos.insertStats(hostStats!!)
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