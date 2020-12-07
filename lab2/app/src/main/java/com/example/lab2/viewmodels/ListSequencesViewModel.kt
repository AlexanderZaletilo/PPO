package com.example.lab2.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.lab2.db.IRepository
import com.example.lab2.db.RoomRepository
import com.example.lab2.db.SequenceWithIntervals
import kotlinx.coroutines.*


class SomeViewModelFactory(private val context: Application): ViewModelProvider.NewInstanceFactory() {
    companion object {
        var instance: SomeViewModelFactory? = null
        var viewModelInstance: ListSequencesViewModel? = null
        fun getInstance(context: Application): SomeViewModelFactory{
            if(instance == null)
                instance = SomeViewModelFactory(context)
            return instance!!
        }
    }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if(viewModelInstance == null)
            viewModelInstance = ListSequencesViewModel(context)
        return viewModelInstance as T
    }
}

class ListSequencesViewModel(val context: Application): AndroidViewModel(context) {
    private var repository: IRepository = RoomRepository(context)
    public lateinit var rawSeqsInts: MutableList<SequenceWithIntervals>
    public var got = false
    public var editingIdx = 0
    public lateinit var seqsInts: MutableList<SequenceWithIntervals>

    suspend fun fetch(){
        repository.fetch()
        rawSeqsInts = repository.getAllSeqInts()
        got = true
    }
    fun sort()
    {
        seqsInts = rawSeqsInts
        for(seqInts_item in seqsInts)
            seqInts_item.intervals.sortBy { it.pos.value }
    }
    fun insert(seqInts: SequenceWithIntervals)
    {
        seqsInts.add(seqInts)
        GlobalScope.launch(Dispatchers.IO){
            repository.insert(seqInts)
        }
    }

    fun update(seqInts: SequenceWithIntervals, deleted: MutableList<Int>)
    {
        seqsInts[editingIdx] = seqInts
        GlobalScope.launch(Dispatchers.IO){
            repository.update(seqInts, deleted)
        }
    }

    fun delete(seqInts: SequenceWithIntervals, pos: Int) {
        seqsInts.removeAt(pos)
        GlobalScope.launch(Dispatchers.IO) {
            repository.delete(seqInts)
        }
    }
}
