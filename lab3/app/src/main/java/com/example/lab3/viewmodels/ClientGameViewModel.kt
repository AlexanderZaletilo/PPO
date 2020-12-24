package com.example.lab3.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.lab3.data.ClientGameFireRepository
import com.example.lab3.game.ShotsType
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ClientGameViewModel: BaseGameViewModel() {
    private var clientRepos =  ClientGameFireRepository.getInstance()
    init {
        repos = clientRepos
        onError = repos.onError
    }
    override fun setUpGame(id: String){
        super.setUpGame(id)
        val hostDataCallback = { name: String, imageUrl: String? ->
            enemyName.value = name
            GlobalScope.launch(Dispatchers.IO) {
                downloadImage(imageUrl!!)
            }
            Unit
        }
        clientRepos.setUpGame(id, hostDataCallback,
            { hostReady.value = true },
            { started.value = true },
            { winnerS: String -> winner.value = winnerS})
        enemyField = Field()
    }
    fun sendShipsToHost()
    {
        clientRepos.sendShipsToHost(yourField!!.ships.toTypedArray())
    }
    fun sendTurnToHost(point: Point)
    {
        clientRepos.sendTurnToHost(point)
    }
    fun onGameStarted(){
        val hostTurnsCallback = { it:Boolean ->
            isHostTurn.value = it
        }
        val cellHostShotCallback = { row: Int, col: Int, value: Int ->
            yourField!![row, col].status = ShotsType.values()[value]
            shotListener?.onShot(Point(row, col), true)
            Unit
        }
        val cellClientShotCallback = { row: Int, col: Int, value: Int ->
            enemyField!![row, col].status = ShotsType.values()[value]
            shotListener?.onShot(Point(row, col), false)
            Unit
        }
        clientRepos.onGameStarted(hostTurnsCallback,
                                  cellHostShotCallback,
                                  cellClientShotCallback)
    }
}