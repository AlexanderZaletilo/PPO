package com.example.lab3.game

class Field {
    val _field: Array<Array<Cell>> = Array(10) {Array(10) {Cell()} }

    operator fun get(row: Int, col: Int): Cell{
        return _field[row][col]
    }
    fun placeShip(ship: Ship)
    {
        if(ship.isHorizontal)
            for(dx in 0 until ship.length) {
                val cell = this[ship.startPoint.row, ship.startPoint.col + dx]
                cell.ship = ship
                cell.part = dx
            }
        else
            for(dx in 0 until ship.length) {
                val cell = this[ship.startPoint.row + dx, ship.startPoint.col]
                cell.ship = ship
                cell.part = dx
            }
    }
    fun deleteShip(ship: Ship)
    {
        val start = ship.startPoint
        if(ship.isHorizontal)
            for(dx in 0 until ship.length)
                this[start.row, start.col + dx].ship = null
        else
            for(dx in 0 until ship.length)
                this[start.row + dx, start.col].ship = null
    }
    fun isAllowedShip(ship: Ship): Boolean{
        val i = ship.startPoint.row - 1
        val j = ship.startPoint.col - 1
        if(ship.isHorizontal) {
            for (di in 0..2)
                if (i + di in 0..9)
                    for (dj in 0 until ship.length + 2)
                        if (j + dj in 0..9 && this[i + di, j + dj].ship != null)
                            return false
        }
        else {
            for (dj in 0..2)
                if (j + dj in 0..9)
                    for (di in 0 until ship.length + 2)
                        if (i + di in 0..9 && this[i + di, j + dj].ship != null)
                            return false
        }
        return true
    }
}