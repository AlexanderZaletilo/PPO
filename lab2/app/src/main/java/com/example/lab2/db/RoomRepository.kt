package com.example.lab2.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*


class RoomRepository(application: Application): IRepository {
    private var mainDao: MainDao
    init{
        val db = AppDatabase.getDatabase(application)
        mainDao = db.getDao()
    }
    private lateinit var allSeqInts: MutableList<SequenceWithIntervals>
    override suspend fun fetch()
    {
        allSeqInts = mainDao.getSeqInts()
    }
    override fun getSeqInts(id: Int) = mainDao.getSpecifiedInts(id)

    override fun getAllSeqInts(): MutableList<SequenceWithIntervals> = allSeqInts

    override suspend fun insert(seqInts: SequenceWithIntervals) {
            mainDao.insertSeqInts(seqInts)
    }

    override suspend fun delete(seqInts: SequenceWithIntervals) {
            mainDao.deleteSeqInts(seqInts)
    }

    override suspend fun update(seqInts: SequenceWithIntervals, deleted: MutableList<Int>) {
            mainDao.updateSeqInts(seqInts, deleted)
    }
}