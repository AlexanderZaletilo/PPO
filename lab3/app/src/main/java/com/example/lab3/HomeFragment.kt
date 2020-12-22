package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.lab3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var errorsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val createButton = view.findViewById<Button>(R.id.game_button_create)
        val IdEditText = view.findViewById<EditText>(R.id.game_lobby_edittext)
        errorsTextView = view.findViewById(R.id.game_lobby_errors)
        database = FirebaseDatabase.getInstance().getReference()
        createButton.setOnClickListener {
            val id = database.push().getKey()
            database.child(id!!).child("host").child("email").setValue(Firebase.auth.currentUser!!.email)
            val action = HomeFragmentDirections.toGame(id, true)
            view.findNavController().navigate(action)
        }
        val joinButton = view.findViewById<Button>(R.id.game_button_join)
        joinButton.setOnClickListener {
            val id = IdEditText.text.toString()
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    errorsTextView.visibility = TextView.VISIBLE
                    if(dataSnapshot.exists() && !dataSnapshot.child("client").child("email").exists()) {
                        database.child(id).child("client").child("email").setValue(Firebase.auth.currentUser!!.email)
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

