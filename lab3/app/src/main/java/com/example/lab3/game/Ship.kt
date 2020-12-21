package com.example.lab3.game

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

}