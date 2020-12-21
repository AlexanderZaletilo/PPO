package com.example.lab3.game

class Cell {
    var ship: Ship? = null
    var part: Int = 0

    override fun toString(): String {
        return "${ship != null}, $part"
    }
}