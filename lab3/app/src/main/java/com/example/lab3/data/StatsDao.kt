package com.example.lab3.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import java.util.*

@TypeConverters(DateConverter::class)
@Dao
interface StatsDao {

    @Transaction
    @Query("SELECT * from stats")
    fun getStats(): LiveData<List<StatsEntity>>

    @Insert
    suspend fun insertStats(stats: StatsEntity)
}