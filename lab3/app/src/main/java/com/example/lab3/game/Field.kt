package com.example.lab3.game

import com.example.lab3.ShotsType

class Field {
    val _field: Array<Array<Cell>> = Array(10) { Array(10) { Cell() } }
    val ships = MutableList<Ship>(0) { Ship(Point(0, 0), Point(0, 0)) }

    operator fun get(row: Int, col: Int) = _field[row][col]
    operator fun get(point: Point) = _field[point.row][point.col]

    fun placeShip(ship: Ship) {
        ships.add(ship)
        if (ship.isHorizontal)
            for (dx in 0 until ship.length) {
                val cell = this[ship.startPoint.row, ship.startPoint.col + dx]
                cell.ship = ship
                cell.part = dx
            }
        else
            for (dx in 0 until ship.length) {
                val cell = this[ship.startPoint.row + dx, ship.startPoint.col]
                cell.ship = ship
                cell.part = dx
            }
    }

    fun importShips(ships: Array<Ship>) {
        for (ship in ships)
            placeShip(ship)
    }

    fun deleteShip(ship: Ship) {
        ships.remove(ship)
        val start = ship.startPoint
        if (ship.isHorizontal)
            for (dx in 0 until ship.length)
                this[start.row, start.col + dx].ship = null
        else
            for (dx in 0 until ship.length)
                this[start.row + dx, start.col].ship = null
    }

    private fun applyFuncToNearbyCells(ship: Ship, func: ((Cell, Point) -> Unit)) {
        val i = ship.startPoint.row - 1
        val j = ship.startPoint.col - 1
        if (ship.isHorizontal) {
            for (di in 0..2)
                if (i + di in 0..9)
                    for (dj in 0 until ship.length + 2)
                        if (j + dj in 0..9)
                            func(this[i + di, j + dj], Point(i + di, j + dj))
        } else {
            for (dj in 0..2)
                if (j + dj in 0..9)
                    for (di in 0 until ship.length + 2)
                        if (i + di in 0..9)
                            func(this[i + di, j + dj], Point(i + di, j + dj))
        }
    }

    fun isAllowedShip(ship: Ship): Boolean {
        var allowed = true
        applyFuncToNearbyCells(ship) { cell, _ ->
            if(allowed)
                allowed = cell.ship == null
        }
        return allowed
    }
    fun markCellsAt(points: MutableList<Point>)
    {
        for(point in points)
            this[point].status = ShotsType.MISSED
    }
    fun getSurroundCells(ship: Ship): MutableList<Point>
    {
        val points = mutableListOf<Point>()
        applyFuncToNearbyCells(ship) { cell, point ->
            if(cell.ship == null && cell.status == ShotsType.NONE)
                points.add(point)
        }
        return points
    }
}