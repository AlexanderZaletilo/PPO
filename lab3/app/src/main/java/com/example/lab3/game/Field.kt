package com.example.lab3.game

class Field {

    val _field: Array<Array<Cell>>

    constructor()
    {
        _field = Array(10) {Array(10) {Cell()} }
        val ships = arrayOf(
                Ship(Point(0, 0), Point(0, 3)),
                Ship(Point(3, 3), Point(5, 3)),
                Ship(Point(7, 7), Point(7, 7)),
        )
        _field[0][0].ship = ships[0]
        _field[0][1].ship = ships[0]
        _field[0][2].ship = ships[0]
        _field[0][3].ship = ships[0]
        _field[3][3].ship = ships[1]
        _field[4][3].ship = ships[1]
        _field[5][3].ship = ships[1]
        _field[7][7].ship = ships[2]
    }
}