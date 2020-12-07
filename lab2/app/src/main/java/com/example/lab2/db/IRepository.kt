package com.example.lab2.db

import androidx.lifecycle.LiveData

interface IRepository {
    suspend fun fetch()
    fun getSeqInts(id: Int): LiveData<SequenceWithIntervals>

    fun getAllSeqInts(): MutableList<SequenceWithIntervals>

    suspend fun insert(seqInts: SequenceWithIntervals)

    suspend fun delete(seqInts: SequenceWithIntervals)

    suspend fun update(seqInts: SequenceWithIntervals, deleted: MutableList<Int>)
}