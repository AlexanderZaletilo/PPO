package com.example.lab2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.R
import com.example.lab2.db.Interval

class IntervalPreviewAdapter(private val context: Context)
    : RecyclerView.Adapter<IntervalPreviewAdapter.IntervalPreviewViewHolder>() {
    private var list: List<Interval> = listOf<Interval>()
    val kind_strings = context.resources.getStringArray(R.array.interval_types)
    val sec_string = context.resources.getString(R.string.sec)
    val reps_string = context.resources.getString(R.string.reps_short)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalPreviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IntervalPreviewViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: IntervalPreviewViewHolder, pos: Int) {
        holder.bind(list[pos], kind_strings)
    }
    fun setData(newList: List<Interval>)
    {
        list = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = list.size

    inner class IntervalPreviewViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_interval_preview, parent, false)){
        var textview: TextView? = null
        init {
            textview = itemView.findViewById(R.id.interval_preview_item)
        }
        fun bind(interval: Interval, kind_strings: Array<String>) {
            textview?.text = "${interval.pos.value.toString()}. ${kind_strings[interval.kind.value!!.ordinal]}: ${interval.time.toString()} ${if(interval.isSeconds.value!!) sec_string else reps_string}"
        }
    }
}