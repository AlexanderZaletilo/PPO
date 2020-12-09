package com.example.lab2.db

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.TypeConverters


@TypeConverters(Converters::class)
class SequenceWithIntervals
{
    @Embedded val seq: Sequence
    @Relation(
        parentColumn = "id",
        entityColumn = "seqId"
    )
    val intervals: MutableList<Interval>
    constructor(seq: Sequence,
                intervals: MutableList<Interval>)
    {
        this.seq = seq
        this.intervals = intervals
    }
    constructor(seqInts: SequenceWithIntervals)
    {
        seq = seqInts.seq.copy()
        intervals = seqInts.intervals.map{ it.copy()}.toMutableList()
    }
    fun equals(seqInts: SequenceWithIntervals): Boolean{
        val tmp = seq.title == seqInts.seq.title && seq.repetitions == seqInts.seq.repetitions &&
                intervals.size == seqInts.intervals.size && seqInts.seq.color.value == seq.color.value
        if(tmp){
            for(i in 0 until intervals.size)
                if(!intervals[i].equals(seqInts.intervals[i]))
                    return false
            return true
        }
        return false
    }
    fun copy(): SequenceWithIntervals {
        return SequenceWithIntervals(seq.copy(), intervals.map{ it.copy()}.toMutableList())
    }

    fun getInfo(): Triple<Int, Int, Byte>
    {
        var time = 0
        var ints = 0
        this.intervals.forEachIndexed { index, it ->
            if(it.kind.value!! == Kind.REPEAT)
            {
                ints += (it.time - 1) * 2
                time += ((if(intervals[index - 1].isSeconds.value!!)
                    intervals[index - 1].time else 0) + (if(intervals[index - 2].isSeconds.value!!)
                            intervals[index - 2].time else 0)) * (it.time - 1)
            }
            else
            {
                ints += 1
                if(it.isSeconds.value!!)
                    time += it.time

            }
        }
        return Triple(time, ints, seq.repetitions)
    }
}