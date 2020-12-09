package com.example.lab2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.lab2.db.SequenceWithIntervals

class SequenceViewModel: ViewModel() {
    public lateinit var originalSeqInts: SequenceWithIntervals
    public lateinit var seqInts: SequenceWithIntervals
    public var fromEditor = false
    public var changed = false
    public var created = false
    public val deleted: MutableList<Int> = mutableListOf<Int>()
    public fun initSequence(initSeqInts: SequenceWithIntervals)
    {
        originalSeqInts = initSeqInts
        seqInts = initSeqInts.copy()
    }
}