package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.R
import com.example.lab3.game.Field


class GameFragment : Fragment() {

    private val args: GameFragmentArgs by navArgs()
    private lateinit var yourRecycler: RecyclerView
    private lateinit var enemyRecycler: RecyclerView
    private lateinit var title: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        yourRecycler = view.findViewById(R.id.your_recycler)
        title = view.findViewById(R.id.game_title_id)
        val manager = GridLayoutManager(requireContext(), 10)
        yourRecycler.layoutManager = manager
        yourRecycler.adapter = GameAdapter(Field());
        yourRecycler.minimumHeight = yourRecycler.width
        if(args.isHost)
        {
            title.visibility = TextView.VISIBLE
            title.text = "ID: ${args.id}"
        }
        return view
    }
}

