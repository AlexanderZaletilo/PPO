package com.example.lab2.db

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "seqs")
data class Sequence(
    @PrimaryKey(autoGenerate = true) var id: Short = 0,
    var title: String,
    val color: MutableLiveData<Int>,
    var repetitions: Byte
){
    constructor(): this(0,"", MutableLiveData<Int>().apply{ value = Color.RED}, 1)
    fun copy(): Sequence {
        return Sequence(id, title, MutableLiveData<Int>().apply { value = color.value}, repetitions)
    }
}