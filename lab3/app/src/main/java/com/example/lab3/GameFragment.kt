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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.NonCancellable.children


class GameFragment : Fragment() {
    private val args: GameFragmentArgs by navArgs()

    private lateinit var yourRecycler: RecyclerView
    private lateinit var enemyRecycler: RecyclerView
    private lateinit var title: TextView
    private lateinit var rotate: CheckBox
    private lateinit var fireButton: Button
    private lateinit var readyCheckBox: CheckBox
    private lateinit var database: DatabaseReference
    private lateinit var viewModel: GameViewModel

    private var cell_width = 0
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        if(viewModel.field == null)
            viewModel.field = Field()
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        cell_width = (requireActivity().windowManager.defaultDisplay.width - 20) / 10 - 2
        yourRecycler = view.findViewById(R.id.your_recycler)
        title = view.findViewById(R.id.game_title_id)
        rotate = view.findViewById(R.id.game_rotate)
        fireButton = view.findViewById(R.id.game_button_fire)
        readyCheckBox = view.findViewById(R.id.game_ready)
        val manager = GridLayoutManager(requireContext(), 10)
        yourRecycler.layoutManager = manager
        yourRecycler.adapter = GameAdapter(viewModel.field!!);
        yourRecycler.minimumHeight = yourRecycler.width
        view.setOnDragListener{ v, event ->
            when (event!!.action) {
                DragEvent.ACTION_DROP -> {
                    val shipView = (event.localState as ShipView)
                    if(event.y - shipView.height > fireButton.y + 15)
                    {
                        shipView.x = event.x - (shipView.width / 2)
                        shipView.y = event.y - (shipView.height / 2)
                        if(shipView.associatedFieldShip != null)
                        {
                            viewModel.field!!.deleteShip(shipView.associatedFieldShip!!)
                            viewModel.placedShips[shipView.length - 1].value = viewModel.placedShips[shipView.length - 1].value!! - 1
                            shipView.associatedFieldShip = null
                        }
                        true
                    }
                    else {
                        if (rotate.isChecked && shipView.associatedFieldShip!!.length > 1) {
                            shipView.rotation = if (shipView.isHorizontal) 90.0F else 0.0F
                            shipView.isHorizontal = !shipView.isHorizontal
                        }
                        false
                    }
                }
                else -> {
                    true
                }
            }
        }
        for(livedata in viewModel.placedShips)
            livedata.observe(requireActivity()) {
                fireButton.isEnabled = viewModel.isAllPlaced()
            }
        yourRecycler.setOnDragListener { v, event ->
            val shipView = event.localState as ShipView
            when (event!!.action) {
                DragEvent.ACTION_DROP -> {
                    val (start, end) = calculateFieldCoords(event.x, event.y, shipView)
                    if(start.col < 0 || start.col > 9 || start.row < 0 || start.row > 9 ||
                            end.col < 0 || end.col > 9 || end.row < 0 || end.row > 9)
                        return@setOnDragListener false
                    val ship = Ship(start, end)
                    if(shipView.associatedFieldShip != null) {
                        viewModel.field!!.deleteShip(shipView.associatedFieldShip!!)
                    }
                    if(viewModel.field!!.isAllowedShip(ship))
                    {
                        viewModel.field!!.placeShip(ship)
                        if(ship.isHorizontal) {
                            shipView.x = yourRecycler.x + cell_width * start.col
                            shipView.y = yourRecycler.y + cell_width * start.row
                        }
                        else
                        {
                            shipView.x = yourRecycler.x + cell_width * start.col - cell_width *  (ship.length - 1) * 0.5F
                            shipView.y = yourRecycler.y + cell_width * start.row + cell_width * (ship.length - 1) * 0.5F
                        }
                        if(shipView.associatedFieldShip == null)
                            viewModel.placedShips[ship.length - 1].value = viewModel.placedShips[ship.length - 1].value!! + 1
                        shipView.associatedFieldShip = ship
                        return@setOnDragListener true
                    }
                    else {
                        if (shipView.associatedFieldShip != null)
                            viewModel.field!!.placeShip(shipView.associatedFieldShip!!)
                        if(rotate.isChecked && shipView.associatedFieldShip!!.length > 1) {
                            shipView.rotation = if (ship.isHorizontal) 90.0F else 0.0F
                            shipView.isHorizontal = !ship.isHorizontal
                        }

                    }
                    return@setOnDragListener false
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
            //database.child("matrix").child(args.id).child("ready").setValue(false)
            //database.child("matrix").child(args.id).child
        }
        else
        {

        }
        return view
    }
    private fun calculateFieldCoords(centerX: Float, centerY: Float, shipView: ShipView): Pair<Point, Point>
    {
        val intX = (centerX / cell_width).toInt()
        val intY = (centerY / cell_width).toInt()
        if(shipView.length == 1)
            return Pair(Point(intY, intX), Point(intY, intX))
        var startdx = 0
        var enddx = 0
        when(shipView.length) {
            2 -> {
                startdx = 0
                enddx = 1
            }
            3 -> {
                startdx = -1
                enddx = 1
            }
            4 -> {
                startdx = -1
                enddx = 2
            }
        }
        return  if(shipView.isHorizontal)
                    Pair(Point(intY, intX + startdx), Point(intY, intX + enddx))
                else
                    Pair(Point(intY + startdx, intX), Point(intY + enddx, intX))
    }
    private fun setUpShipPalette(view: View)
    {
        val viewListener = View.OnLongClickListener{ v: View ->
            val ship = v as ShipView
            if(rotate.isChecked && ship.length > 1)
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

