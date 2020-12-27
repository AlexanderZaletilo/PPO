package com.example.lab3.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.lab3.ui.fragments.HomeFragmentDirections
import com.example.lab3.R
import com.example.lab3.data.ClientGameFireRepository
import com.example.lab3.data.HostGameFireRepository


class HomeFragment : Fragment() {
    private lateinit var errorsTextView: TextView
    private var hostRepos = HostGameFireRepository.getInstance()
    private var clientRepos =  ClientGameFireRepository.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        val createButton = view.findViewById<Button>(R.id.game_button_create)
        val IdEditText = view.findViewById<EditText>(R.id.game_lobby_edittext)
        errorsTextView = view.findViewById(R.id.game_lobby_errors)
        hostRepos.setUpUser()
        clientRepos.setUpUser()
        createButton.setOnClickListener {
            val action = HomeFragmentDirections.toGame(hostRepos.setUpGameLobby(), true)
            view.findNavController().navigate(action)
        }
        val joinButton = view.findViewById<Button>(R.id.game_button_join)
        joinButton.setOnClickListener {
            val id = IdEditText.text.toString()
            val joinCallback = { joined: Boolean ->
                if(joined) {
                    val action = HomeFragmentDirections.toGame(id, false)
                    view.findNavController().navigate(action)
                }
                else
                {
                    errorsTextView.text = "Invalid ID"
                    errorsTextView.visibility = TextView.VISIBLE
                }
            }
            clientRepos.tryJoin(id, joinCallback)
        }
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_to_user -> {
                requireView().findNavController().navigate(HomeFragmentDirections.toUser())
            }
            R.id.action_to_stats -> {
                requireView().findNavController().navigate(HomeFragmentDirections.toStats())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

