package com.example.lab3.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.R
import com.example.lab3.adapters.StatsAdapter
import com.example.lab3.data.StatsRepository


class StatsFragment : Fragment() {
    private lateinit var repos: StatsRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        repos = StatsRepository(requireActivity().application)
        val recycler = view.findViewById<RecyclerView>(R.id.stats_recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        repos.getStats().observe(requireActivity()) {
            val adapter = StatsAdapter(it)
            recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        return view
    }

}

