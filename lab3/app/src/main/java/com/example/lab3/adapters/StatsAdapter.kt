package com.example.lab3.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.R
import com.example.lab3.data.StatsEntity
import com.example.lab3.game.Field
import com.example.lab3.game.ShotsType


class StatsAdapter(val list: List<StatsEntity>) : RecyclerView.Adapter<StatsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.stats_item, viewGroup, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        viewHolder.bind(list[i])
    }

    override fun getItemCount() = list.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val resultTextView = itemView.findViewById<TextView>(R.id.stats_result)
        val startedTextView = itemView.findViewById<TextView>(R.id.stats_started)
        val endedTextView = itemView.findViewById<TextView>(R.id.stats_ended)
        val shipsTextView = itemView.findViewById<TextView>(R.id.stats_ships_count)
        val opponentTextView = itemView.findViewById<TextView>(R.id.stats_opponent)
        val shotsTextView = itemView.findViewById<TextView>(R.id.stats_shotshits)

        fun bind(stats: StatsEntity)
        {
            if(stats.isWin)
            {
                itemView.setBackgroundColor(Color.GREEN)
                resultTextView.text = "Won"
            }
            else{
                itemView.setBackgroundColor(Color.RED)
                resultTextView.text = "Lose"
            }
            startedTextView.text = stats.started.toString()
            endedTextView.text = stats.ended.toString()
            opponentTextView.text = stats.against
            shipsTextView.text = "${stats.ships_1}/4 ${stats.ships_2}/3 ${stats.ships_3}/2 ${stats.ships_4}/1"
            shotsTextView.text = "${stats.hits}/${stats.shots}"
        }
    }

}

