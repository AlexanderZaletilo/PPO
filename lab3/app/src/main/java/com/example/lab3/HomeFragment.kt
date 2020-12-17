package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lab3.R


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

   /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_sequence_list_menu, menu)
        super.onCreateOptionsMenu(menu!!, inflater)
    }*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       /* when (item.itemId) {

        }*/
        return super.onOptionsItemSelected(item)
    }
}

