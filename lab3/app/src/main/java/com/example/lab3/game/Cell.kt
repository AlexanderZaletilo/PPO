package com.example.lab3.game

import com.example.lab3.ShotsType

class Cell {
    var ship: Ship? = null
    var part: Int = 0
    var status = ShotsType.NONE
    override fun toString(): String {
        return "${ship != null}, $part"
    }
}