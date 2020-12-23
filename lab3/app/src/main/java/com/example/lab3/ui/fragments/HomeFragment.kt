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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var errorsTextView: TextView
    private lateinit var user: FirebaseUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        val createButton = view.findViewById<Button>(R.id.game_button_create)
        val IdEditText = view.findViewById<EditText>(R.id.game_lobby_edittext)
        errorsTextView = view.findViewById(R.id.game_lobby_errors)
        database = FirebaseDatabase.getInstance().getReference()
        user = Firebase.auth.currentUser!!
        createButton.setOnClickListener {
            val id = database.push().getKey()
            database.child(id!!).child("host")
                    .setValue(mapOf("name" to user.displayName,
                    "imageUrl" to user.providerData[0].photoUrl.toString()))
            val action = HomeFragmentDirections.toGame(id, true)
            view.findNavController().navigate(action)
        }
        val joinButton = view.findViewById<Button>(R.id.game_button_join)
        joinButton.setOnClickListener {
            val id = IdEditText.text.toString()
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    errorsTextView.visibility = TextView.VISIBLE
                    if(dataSnapshot.exists() && !dataSnapshot.child("client").exists()) {
                        database.child(id!!).child("client")
                                .setValue(mapOf("name" to user.displayName,
                                        "imageUrl" to user.providerData[0].photoUrl.toString()))
                        val action = HomeFragmentDirections.toGame(id, false)
                        view.findNavController().navigate(action)
                    }
                    else {
                        errorsTextView.text = "Invalid ID"
                        errorsTextView.visibility = TextView.VISIBLE
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    errorsTextView.visibility = TextView.VISIBLE
                    errorsTextView.text = databaseError.toException().toString()
                }
            }
            database.child(id).addListenerForSingleValueEvent(listener)
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
        }
        return super.onOptionsItemSelected(item)
    }
}

