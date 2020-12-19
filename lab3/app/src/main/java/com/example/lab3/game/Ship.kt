package com.example.lab3.game

class Ship {
    val length: Int
    val isHorizontal: Boolean
    val isBrokenParts: Array<Boolean>
    val startPoint: Point
    constructor(start: Point, end: Point)
    {
        startPoint = start
        isHorizontal = start.row == end.row
        length = if(isHorizontal)
                    kotlin.math.abs(start.row - end.row)
                else
                    kotlin.math.abs(start.col - end.col)
        isBrokenParts = Array<Boolean>(length) { false }
    }

}