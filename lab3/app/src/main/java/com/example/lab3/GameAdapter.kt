package com.example.lab3

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.game.Cell
import com.example.lab3.game.Field
import java.lang.String
import java.util.*


class GameAdapter(val field: Field) : RecyclerView.Adapter<GameAdapter.MyViewHolder>() {

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

    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        val row = i / 10
        val col = i % 10
        val cell = field._field[row][col]
        viewHolder.cellImageView.setImageResource(R.drawable.ic_item_back)
        if(cell.ship != null)
            viewHolder.cellImageView.isSelected = true
    }

    override fun getItemCount(): Int {
        return 100
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cellImageView: ImageView = itemView.findViewById(R.id.cell_image_view)
    }

}

