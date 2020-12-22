package com.example.lab3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.R
import com.example.lab3.game.Field
import com.example.lab3.game.ShotsType


class GameAdapter(val field: Field, val isClickable: Boolean) : RecyclerView.Adapter<GameAdapter.MyViewHolder>() {
    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    private lateinit var recycler: RecyclerView
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.game_item, viewGroup, false)
        view.layoutParams.height = recycler.measuredWidth / 10
        view.layoutParams.width = recycler.measuredWidth / 10
        return MyViewHolder(view)
    }
    fun makeSelection(pos: Int)
    {
        selectedItemPos = pos
        if(selectedItemPos == lastItemSelectedPos)
        {
            selectedItemPos = -1
            lastItemSelectedPos = -1
            notifyItemChanged(pos)
            return
        }
        if (lastItemSelectedPos == -1)
            lastItemSelectedPos = selectedItemPos
        else {
            notifyItemChanged(lastItemSelectedPos)
            lastItemSelectedPos = selectedItemPos
        }
        notifyItemChanged(selectedItemPos)
    }
    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        val row = i / 10
        val col = i % 10
        val cell = field[row, col]
        viewHolder.cellImageBack.setBackgroundResource(
            if(selectedItemPos == i) R.drawable.ic_item_back_selected else R.drawable.ic_item_back
        )
        viewHolder.cellImageBack.isSelected = cell.ship != null
        viewHolder.cellImageView.setImageResource(
            when(cell.status)
            {
                ShotsType.NONE -> R.drawable.ic_baseline_empty_24
                ShotsType.HIT -> R.drawable.ic_baseline_hit_24
                ShotsType.MISSED -> R.drawable.ic_baseline_missed_24
            }
        )

    }

    override fun getItemCount(): Int {
        return 100
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cellImageView: ImageView = itemView.findViewById(R.id.cell_image_view)
        val cellImageBack: View = itemView.findViewById(R.id.cell_image_back)
        init {
            if(isClickable)
                cellImageBack.setOnClickListener {
                    makeSelection(adapterPosition)
                }
        }
    }

}

