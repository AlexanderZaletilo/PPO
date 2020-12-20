package com.example.lab3

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.game.Field
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.NonCancellable.children


class GameFragment : Fragment() {
    private val args: GameFragmentArgs by navArgs()
    private lateinit var yourRecycler: RecyclerView
    private lateinit var enemyRecycler: RecyclerView
    private lateinit var title: TextView
    private lateinit var database: DatabaseReference
    private lateinit var rotate: CheckBox
    private lateinit var fireButton: Button
    private var cell_width = 0
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        cell_width = (requireActivity().windowManager.defaultDisplay.width - 20) / 10
        yourRecycler = view.findViewById(R.id.your_recycler)
        title = view.findViewById(R.id.game_title_id)
        rotate = view.findViewById(R.id.game_rotate)
        fireButton = view.findViewById(R.id.game_button_fire)
        val manager = GridLayoutManager(requireContext(), 10)
        yourRecycler.layoutManager = manager
        yourRecycler.adapter = GameAdapter(Field());
        yourRecycler.minimumHeight = yourRecycler.width
        view.setOnDragListener{ v, event ->
            when (event!!.action) {
                DragEvent.ACTION_DROP -> {
                    val ship = (event.localState as View)
                    if(event.y - ship.height > fireButton.y + 15)
                    {
                        ship.x = event.x - (ship.width / 2)
                        ship.y = event.y - (ship.height / 2)
                        true
                    }
                    else
                        false
                }
                else -> {
                    true
                }
            }
        }
        yourRecycler.setOnDragListener { v, event ->
            val ship = event.localState as View
            when (event!!.action) {
                DragEvent.ACTION_DROP -> {
                    ship.x = event.x - (ship.width / 2) + yourRecycler.x
                    ship.y = event.y - (ship.height / 2) + yourRecycler.y
                    true
                }
                else -> {
                    true
                }
            }
        }
        setUpShipPalette(view)
        database = FirebaseDatabase.getInstance().reference
        if(args.isHost)
        {
            title.visibility = TextView.VISIBLE
            title.text = "ID: ${args.id}"
            database.child("matrix").child(args.id).child("ready").setValue(false)
        }
        return view
    }

    private fun setUpShipPalette(view: View)
    {
        val viewListener = View.OnLongClickListener{ v: View ->
            val ship = v as ShipView
            if(rotate.isChecked)
            {
                v.rotation = if(ship.isHorizontal) 90.0F else 0.0F
                ship.isHorizontal = !ship.isHorizontal
            }
            val myShadow = MyDragShadowBuilder(v)
            v.startDrag(null, myShadow, v,0)
            true
        }
        val ship1_1 = view.findViewById<ShipView>(R.id.ship_1_1)
        val ship1_2 = view.findViewById<ShipView>(R.id.ship_1_2)
        val ship1_3 = view.findViewById<ShipView>(R.id.ship_1_3)
        val ship1_4 = view.findViewById<ShipView>(R.id.ship_1_4)
        for(item in sequenceOf(ship1_1, ship1_2, ship1_3, ship1_4)) {
            item.layoutParams.width = cell_width
            item.length = 1
        }
        val ship2_1 = view.findViewById<ShipView>(R.id.ship_2_1)
        val ship2_2 = view.findViewById<ShipView>(R.id.ship_2_2)
        val ship2_3 = view.findViewById<ShipView>(R.id.ship_2_3)
        for(item in sequenceOf(ship2_1, ship2_2, ship2_3)) {
            item.layoutParams.width = 2 * cell_width
            item.length = 2
        }
        val ship3_1 = view.findViewById<ShipView>(R.id.ship_3_1)
        val ship3_2 = view.findViewById<ShipView>(R.id.ship_3_2)
        for(item in sequenceOf(ship3_1, ship3_2)) {
            item.layoutParams.width = 3 * cell_width
            item.length = 3
        }
        val ship4_1 = view.findViewById<ShipView>(R.id.ship_4_1)
        ship4_1.layoutParams.width = 4 * cell_width
        ship4_1.length = 4
        for(item in sequenceOf(ship1_1, ship1_2, ship1_3, ship1_4, ship2_1, ship2_2, ship2_3, ship3_1, ship3_2, ship4_1)) {
            item.layoutParams.height = cell_width
            item.setOnLongClickListener(viewListener)
        }
    }
    private fun setUpMatrix(matrix: DatabaseReference)
    {

    }
}

private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    override fun onDrawShadow(canvas: Canvas) {
        canvas.rotate(view.rotation)
        super.onDrawShadow(canvas)
    }
}

