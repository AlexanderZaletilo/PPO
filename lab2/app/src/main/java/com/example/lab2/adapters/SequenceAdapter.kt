package com.example.lab2.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.*
import com.example.lab2.db.SequenceWithIntervals

class SequenceAdapter(private val context: Context,
                      var list: MutableList<SequenceWithIntervals>,
                      private val listener: ListItemClickListener)
: RecyclerView.Adapter<SequenceAdapter.SequenceViewHolder>()
{
    val total_string = context.resources.getString(R.string.total)
    val ints_string = context.resources.getString(R.string.intervals)
    val sets_string = context.resources.getString(R.string.sets)
    interface ListItemClickListener{
        fun onListItemClick(position: Int, holder: SequenceViewHolder);
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SequenceViewHolder(inflater, parent, context)
    }
    override fun onBindViewHolder(holder: SequenceViewHolder, position: Int) {
        val seqInts: SequenceWithIntervals = list[position]
        holder.bind(seqInts)
    }
    override fun getItemCount(): Int = list.size
    inner class SequenceViewHolder (inflater: LayoutInflater, parent: ViewGroup, context: Context) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_sequence, parent, false)), View.OnClickListener {
        private var seqTitle: TextView? = null
        private var seqInfo: TextView? = null
        private var layout: ConstraintLayout? = null
        private var dots: TextView? = null
        private var recycler: RecyclerView? = null
        private var recycler_adapter: IntervalPreviewAdapter? = null
        val actionsButton: ImageButton
        init {
            seqTitle = itemView.findViewById(R.id.sequence_item_title)
            seqInfo = itemView.findViewById(R.id.sequence_item_other_info)
            actionsButton = itemView.findViewById(R.id.sequence_item_menu)
            layout = itemView.findViewById(R.id.seq_item_layout)
            dots = itemView.findViewById(R.id.sequence_item_dots)
            recycler = itemView.findViewById(R.id.interval_preview_recycler)
            recycler_adapter = IntervalPreviewAdapter(context)
            recycler!!.adapter = recycler_adapter
            recycler!!.layoutManager = LinearLayoutManager(context)
            itemView.setOnClickListener(this)
            actionsButton.setOnClickListener(this)
        }

        fun bind(seqInts: SequenceWithIntervals) {
            seqTitle?.text = seqInts.seq.title
            val (time, ints, reps) = seqInts.getInfo()
            seqInfo?.text = "%02d:%02d:%02d $total_string ; $ints $ints_string ; $reps $sets_string".format(
                time / 3600, time / 60 % 60, time % 60
            )
            if(!((context as MainActivity).isDark!!))
                layout?.setBackgroundColor(seqInts.seq.color.value!!)
            recycler_adapter?.setData(seqInts.intervals.subList(0,
                5.coerceAtMost(seqInts.intervals.size)
            ))
            if(seqInts.intervals.size <= 5)
                dots?.visibility = View.GONE
            else
                dots?.visibility = View.VISIBLE
        }
        override fun onClick(v: View) {
            val position = adapterPosition;
            listener.onListItemClick(position, this);
        }
    }
}
