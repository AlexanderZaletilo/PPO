package com.example.lab3.game

class Field {

    val _field: Array<Array<Cell>>

    constructor()
    {
        _field = Array(10) {Array(10) {Cell()} }
    }
}