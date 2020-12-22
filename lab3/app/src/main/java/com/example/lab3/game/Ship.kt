package com.example.lab3.game

import android.util.JsonReader
import org.json.JSONArray
import org.json.JSONObject


class Ship {
    val length: Int
    val isHorizontal: Boolean
    val isBrokenParts: Array<Boolean>
    val startPoint: Point
    val endPoint: Point
    constructor(start: Point, end: Point)
    {
        startPoint = start
        endPoint = end
        isHorizontal = start.row == end.row
        length = if(isHorizontal)
                    end.col - start.col + 1
                else
                    end.row - start.row + 1
        isBrokenParts = Array<Boolean>(length) { false }
    }
    constructor(start: Point, end: Point, length: Int, isHorizontal: Boolean)
    {
        this.length = length
        this.isHorizontal = isHorizontal
        this.startPoint = start
        this.endPoint = end
        isBrokenParts = Array<Boolean>(length) { false }
    }
    fun isBroken(): Boolean
    {
        for(part in isBrokenParts)
            if(!part)
                return false
        return true
    }
    companion object {
        fun toJsonString(ship: Ship): String {
            return """{"length": ${ship.length},"isHorizontal": ${ship.isHorizontal},"startPoint": ${Point.toJsonString(ship.startPoint)},"endPoint": ${Point.toJsonString(ship.endPoint)}}"""
        }
        fun fromJsonObject(json: JSONObject): Ship {
            return Ship(Point.fromJsonObject(json.getJSONObject("startPoint")),
                        Point.fromJsonObject(json.getJSONObject("endPoint")),
                        json.getInt("length"),
                        json.getBoolean("isHorizontal"))
        }
        fun ArrayToJsonString(ships: Array<Ship>): String {
            var ships_str = ""
            for((i, ship) in ships.withIndex()) {
                ships_str += Ship.toJsonString(ship)
                if(i != 9)
                    ships_str += ','
            }
            return "[$ships_str]"
        }
        fun fromArrayJsonString(string: String): Array<Ship> {
            val json = JSONArray(string)
            return Array<Ship>(10) {Ship.fromJsonObject(json.getJSONObject(it))}
        }
    }
}