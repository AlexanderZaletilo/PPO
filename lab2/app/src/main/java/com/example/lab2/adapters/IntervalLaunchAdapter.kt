package com.example.lab2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.LaunchActivity
import com.example.lab2.LaunchService
import com.example.lab2.R
import com.example.lab2.db.Interval
import com.example.lab2.viewmodels.LaunchViewModel

class IntervalLaunchAdapter(private val context: Context, val service: LaunchService)
    : RecyclerView.Adapter<IntervalLaunchAdapter.IntervalLaunchViewHolder>() {
    private var list: List<Interval> = listOf<Interval>()
    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    private lateinit var recycler: RecyclerView
    private lateinit var manager: LinearLayoutManager
    val kind_strings = context.resources.getStringArray(R.array.interval_types)
    val dimen = context.resources.getDimension(R.dimen.launch_textview)
    val sec_string = context.resources.getString(R.string.sec)
    val reps_string = context.resources.getString(R.string.reps_short)
    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler);
        this.recycler = recycler
        manager = recycler.layoutManager as LinearLayoutManager
    }
    fun makeSelection(pos: Int)
    {
        selectedItemPos = pos
        if (lastItemSelectedPos == -1)
            lastItemSelectedPos = selectedItemPos
        else {
            notifyItemChanged(lastItemSelectedPos)
            lastItemSelectedPos = selectedItemPos
        }
        notifyItemChanged(selectedItemPos)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalLaunchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IntervalLaunchViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: IntervalLaunchViewHolder, pos: Int) {
        holder.textview?.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        holder.textview?.textSize = dimen
        holder.bind(list[pos], kind_strings, pos == selectedItemPos)
    }
    fun setData(newList: List<Interval>)
    {
        list = newList
        notifyDataSetChanged()
        recycler.setHasFixedSize(true)
    }
    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class IntervalLaunchViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_interval_launch, parent, false)) {
        var textview: TextView? = null
        init {
            textview = itemView.findViewById(R.id.interval_preview_item_launch)
            itemView.setOnClickListener {
                service.setPosition(adapterPosition)
            }
        }
            fun bind(interval: Interval, kind_strings: Array<String>, activated: Boolean) {
                textview?.text = "${interval.pos.value.toString()}. ${kind_strings[interval.kind.value!!.ordinal]}: ${interval.time.toString()} ${if(interval.isSeconds.value!!) sec_string else reps_string}"
                itemView.isActivated = activated
            }
        }
}