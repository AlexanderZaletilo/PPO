package com.example.lab3.game

class Field {

    val _field: Array<Array<Cell>> = Array(10) {Array(10) {Cell()} }

    operator fun get(row: Int, col: Int): Cell{
        return _field[row][col]
    }
}