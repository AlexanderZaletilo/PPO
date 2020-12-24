package com.example.lab3.game

import org.json.JSONObject
import java.util.*

class GameStats(val against: String, val started: Date) {
    var hits = 0
    var shots = 0
    lateinit var ended: Date
    var isWin = false
    var shipsDestroyedCount = Array<Int>(4) { 0}

    companion object {
        fun toJsonString(stats: GameStats): String
        {
            var ships = "["
            for( (i, ship) in stats.shipsDestroyedCount.withIndex()) {
                ships += "$ship"
                if (i != 3)
                    ships += ","
            }
            ships += "]"
            return """{"hits": ${stats.hits}, "shots": ${stats.shots}, "isWin": ${stats.isWin}, "against": ${stats.against}, "ships": $ships, "started": ${stats.started.time}, "ended": ${stats.ended.time} }"""
        }

        fun fromJSONString(jsonString: String): GameStats{
            val json = JSONObject(jsonString)
            val stats = GameStats(json.getString("against"), Date(json.getLong("started")))
            stats.ended = Date(json.getLong("ended"))
            stats.isWin = json.getBoolean("isWin")
            stats.hits = json.getInt("hits")
            stats.shots = json.getInt("shots")
            val arr = json.getJSONArray("ships")
            stats.shipsDestroyedCount = Array<Int>(4) { arr.getInt(it)}
            return stats
        }
    }
}