package com.example.lab3.game

class Ship {
    val length: Int
    val isHorizontal: Boolean
    val isBrokenParts: Array<Boolean>
    val startPoint: Point
    constructor(start: Point, end: Point)
    {
        startPoint = start
        isHorizontal = start.first == end.first
        length = if(isHorizontal)
                    kotlin.math.abs(start.second - end.second)
                else
                    kotlin.math.abs(start.first - end.first)
        isBrokenParts = Array<Boolean>(length) { false }
    }

}