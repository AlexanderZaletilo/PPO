package com.example.lab2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView

import com.example.lab2.R
import com.example.lab2.db.Interval
import com.example.lab2.db.Kind

class IntervalAdapter(
    val list: MutableList<Interval>,
    private val context: Context,
    private val listener: OnClickListener
        )
    : RecyclerView.Adapter<IntervalAdapter.IntervalViewHolder>() {
    val kind_strings = context.resources.getStringArray(R.array.interval_types)
    interface OnClickListener{
        fun onClick(position: Int, holder: IntervalViewHolder)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IntervalViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: IntervalViewHolder, pos: Int) {
        val interval: Interval = list[pos]
        holder.bind(interval)
        interval.pos.observe(holder.itemView.context as LifecycleOwner) {
            holder.pos.text = it.toString()
        }
        interval.kind.observe(holder.itemView.context as LifecycleOwner) {
            holder.type.text = kind_strings[it.ordinal]
        }
        interval.isSeconds.observe(holder.itemView.context as LifecycleOwner) {
            holder.isSeconds.isSelected = it
        }
    }
    override fun getItemCount(): Int = list.size

    inner class IntervalViewHolder (inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_interval, parent, false)),
        View.OnClickListener{
        var pos: TextView = itemView.findViewById(R.id.interval_textview_pos)
        var type: TextView = itemView.findViewById(R.id.interval_textview_type)
        var isSeconds: ImageButton = itemView.findViewById(R.id.interval_button_isseconds)
        var menu: ImageButton = itemView.findViewById(R.id.interval_imbutton_menu)
        var plus: ImageButton = itemView.findViewById(R.id.interval_buton_plus)
        var minus: ImageButton = itemView.findViewById(R.id.interval_button_minus)
        var reps: EditText = itemView.findViewById(R.id.interval_edittext_reps)
        init {
            menu.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            listener.onClick(adapterPosition, this)
        }
        fun bind(interval: Interval) {
            reps.setText(interval.time.toString())
            plus.setOnClickListener {
                if(interval.time.toInt() < 9999){
                    interval.time++
                    reps.setText(interval.time.toString())
                }
            }
            minus.setOnClickListener {
                if(interval.time.toInt() > 1){
                    interval.time--
                    reps.setText(interval.time.toString())
                }
            }
            isSeconds.setOnClickListener {
                if(interval.kind.value != Kind.REPEAT)
                    interval.isSeconds.value = interval.isSeconds.value!! xor true
            }
            reps.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && (reps.text.toString().isEmpty() || (reps.text.toString().toInt() == 0)))
                {
                    interval.time = 1
                    reps.text.clear()
                    reps.text.insert(0, "1")
                }
            }
            reps.doOnTextChanged { text, _, _, _ ->
                if (text!!.isNotEmpty())
                    interval.time = text.toString().toShort()
            }
        }
    }
}
