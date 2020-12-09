package com.example.lab2.db


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
@TypeConverters(Converters::class)
@Dao
interface MainDao {
    @Transaction
    @Query("SELECT * FROM seqs")
    suspend fun getSeqInts(): MutableList<SequenceWithIntervals>

    @Query("DELETE FROM intervals WHERE intervalId IN (:deleted)")
    suspend fun deleteIntervalsById(deleted: List<Int>)

    @Transaction
    @Query("SELECT * FROM seqs WHERE id = :id")
    fun getSpecifiedInts(id: Int): LiveData<SequenceWithIntervals>

    @Delete
    suspend fun deleteIntervals(deleted: List<Interval>)

    @Delete
    suspend fun deleteSequence(seq: Sequence)

    @Insert
    suspend fun insertSequence(seq: Sequence): Long

    @Insert
    suspend fun insertInterval(int: Interval): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInterval(int: Interval)

    @Update
    suspend fun updateSequence(seq: Sequence)

    @Transaction
    suspend  fun updateSeqInts(seqInts: SequenceWithIntervals, deleted: MutableList<Int>)
    {
        updateSequence(seqInts.seq)
        for(interval in seqInts.intervals)
        {
            if(interval.intervalId == 0)
            {
                interval.seqId = seqInts.seq.id
                val id = insertInterval(interval)
                interval.intervalId = id.toInt()
            }
            else
                updateInterval(interval)
        }
        if(deleted.size > 0)
            deleteIntervalsById(deleted)
    }

    @Transaction
    suspend fun insertSeqInts(seqInts: SequenceWithIntervals)
    {
        val seqId = insertSequence(seqInts.seq)
        for(interval in seqInts.intervals)
        {
            interval.seqId = seqId.toShort()
            insertInterval(interval)
        }
    }
    @Transaction
    suspend fun deleteSeqInts(seqInts: SequenceWithIntervals)
    {
        deleteIntervals(seqInts.intervals)
        deleteSequence(seqInts.seq)
    }
}