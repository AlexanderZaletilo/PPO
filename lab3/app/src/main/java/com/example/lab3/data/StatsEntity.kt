package com.example.lab3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@TypeConverters(DateConverter::class)
@Entity(tableName = "stats")
data class StatsEntity (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val hits: Int,
    val shots: Int,
    val started: Date,
    val ended: Date,
    val against: String,
    val isWin: Boolean,
    val ships_1: Int,
    val ships_2: Int,
    val ships_3: Int,
    val ships_4: Int
)