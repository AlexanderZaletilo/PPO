package com.example.lab3.game

import org.json.JSONObject

data class Point(val row: Int, val col: Int)
{
    companion object {
        fun toJsonString(point: Point): String {
            return """{"row": ${point.row},"col": ${point.col}}"""
        }
        fun fromJsonObject(json: JSONObject): Point {
            return Point(json.getInt("row"), json.getInt("col"))
        }
    }
}
