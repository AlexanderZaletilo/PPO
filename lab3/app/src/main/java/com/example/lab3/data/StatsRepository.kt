package com.example.lab3.data

import android.app.Application
import com.example.lab3.game.GameStats


class StatsRepository(application: Application) {
    private var mainDao: StatsDao

    init {
        val db = StatsDatabase.getDatabase(application)
        mainDao = db.getDao()
    }

    fun getStats() = mainDao.getStats()

    suspend fun insertStats(stats: GameStats)
    {
        mainDao.insertStats(
            StatsEntity(
                0,
                stats.hits,
                stats.shots,
                stats.started,
                stats.ended,
                stats.against,
                stats.isWin,
                stats.shipsDestroyedCount[0],
                stats.shipsDestroyedCount[1],
                stats.shipsDestroyedCount[2],
                stats.shipsDestroyedCount[3],
            )
        )
    }
}