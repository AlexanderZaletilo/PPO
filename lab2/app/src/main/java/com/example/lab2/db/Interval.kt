package com.example.lab2.db

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters


@TypeConverters(Converters::class)
@Entity(tableName = "intervals")
data class Interval(
    @PrimaryKey(autoGenerate = true) var intervalId: Int = 0,
    val kind: MutableLiveData<Kind>,
    var time: Short,
    var isSeconds: MutableLiveData<Boolean>,
    var seqId: Short,
    var pos: MutableLiveData<Byte>)
{
    fun copy(): Interval {
        return Interval(
            intervalId,
            MutableLiveData<Kind>().apply {value = kind.value},
            time, MutableLiveData<Boolean>().apply {value = isSeconds.value}, seqId,
            MutableLiveData<Byte>().apply{ value = pos.value})
    }
    constructor(int: Interval) :
            this(int.intervalId, MutableLiveData<Kind>().apply{ value = int.kind.value},
                int.time, MutableLiveData<Boolean>().apply{ value = int.isSeconds.value},
                int.seqId, MutableLiveData<Byte>().apply{ value = int.pos.value})
    constructor(pos_arg: Byte) : this(
        0,
        MutableLiveData<Kind>().apply{value = Kind.PREPARE},
        1,
        MutableLiveData<Boolean>().apply{value = true},
        0,
        MutableLiveData<Byte>().apply{ value =  pos_arg }
    )
    fun equals(int: Interval): Boolean
    {
        return int.pos.value == this.pos.value && int.isSeconds.value == this.isSeconds.value &&
                int.time == this.time && int.kind.value == this.kind.value
    }

}