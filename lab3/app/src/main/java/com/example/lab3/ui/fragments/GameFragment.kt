package com.example.lab3.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.*
import com.example.lab3.adapters.GameAdapter
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.example.lab3.game.Ship
import com.example.lab3.ui.MyDragShadowBuilder
import com.example.lab3.ui.ShipView
import com.example.lab3.ui.activities.MainActivity
import com.example.lab3.viewmodels.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class GameFragment : Fragment(), BaseGameViewModel.onShotListener,
    ExitDialogFragment.ExitDialogListener {
    private val args: GameFragmentArgs by navArgs()

    private lateinit var yourRecycler: RecyclerView
    private lateinit var yourAdapter: GameAdapter
    private lateinit var enemyRecycler: RecyclerView
    private lateinit var enemyAdapter: GameAdapter
    private lateinit var layout: ConstraintLayout
    private lateinit var title: TextView
    private lateinit var rotateCheckBox: CheckBox
    private lateinit var fireButton: Button
    private lateinit var readyButton: Button
    private lateinit var shipViews: List<ShipView>
    private lateinit var opponentNameTextView: TextView
    private lateinit var opponentImageButton: ImageButton

    private lateinit var hostViewModel: HostGameViewModel
    private lateinit var clientViewModel: ClientGameViewModel
    private lateinit var viewModel: BaseGameViewModel
    private lateinit var main_activity: MainActivity
    private var cell_width = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        main_activity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = this
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(viewModel.winner.value != "" || viewModel.onError.value == true || !viewModel.started.value!! )
                {
                    findNavController().navigateUp()
                    return@handleOnBackPressed
                }
                else
                {
                    ExitDialogFragment(this@GameFragment)
                        .show(main_activity.supportFragmentManager, "exitDialogFragment")
                }
            }
        }
        main_activity.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun findViews(view: View)
    {
        cell_width = (main_activity.windowManager.defaultDisplay.width - 20) / 10 - 2
        yourRecycler = view.findViewById(R.id.your_recycler)
        enemyRecycler = view.findViewById(R.id.enemy_recycler)
        title = view.findViewById(R.id.game_title_id)
        rotateCheckBox = view.findViewById(R.id.game_rotate)
        layout = view.findViewById(R.id.game_layout)
        fireButton = view.findViewById(R.id.game_button_fire)
        readyButton = view.findViewById(R.id.game_ready)
        opponentNameTextView = view.findViewById(R.id.game_opponent)
        opponentImageButton = view.findViewById(R.id.game_opponent_imagebutton)
        opponentImageButton.isEnabled = false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(args.isHost)
        {
            hostViewModel = ViewModelProvider(this, HostViewModelFactory.getInstance(main_activity.application))
                .get(HostGameViewModel::class.java)
            viewModel = hostViewModel
        }
        else
        {
            clientViewModel = ViewModelProvider(this, ClientViewModelFactory.getInstance(main_activity.application))
                .get(ClientGameViewModel::class.java)
            viewModel = clientViewModel
        }
        if(viewModel.yourField == null)
            viewModel.yourField = Field()
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        findViews(view)
        val manager = GridLayoutManager(main_activity, 10)
        yourRecycler.layoutManager = manager
        yourAdapter = GameAdapter(viewModel.yourField!!, false)
        yourRecycler.adapter = yourAdapter
        val manager2 = GridLayoutManager(main_activity, 10)
        enemyRecycler.layoutManager = manager2
        view.setOnDragListener(this::layoutDragListener)
        for(livedata in viewModel.placedShips)
            livedata.observe(main_activity) {
                readyButton.isEnabled = viewModel.isAllPlaced()
            }
        yourRecycler.setOnDragListener(this::recyclerDragListener)
        setUpShipPalette(view)
        viewModel.setUpGame(args.id)
        viewModel.enemyImage.observe(main_activity) {
            opponentImageButton.isEnabled = true
            opponentImageButton.setOnClickListener {
                ProfileDialogFragment(
                    viewModel.enemyImage.value!!,
                    viewModel.enemyName.value!!
                ).show(main_activity.supportFragmentManager, "opponentFragment")
            }
        }
        viewModel.enemyName.observe(main_activity) {
            opponentNameTextView.text = it
        }
        viewModel.onError.observe(main_activity) {
            showInfo(R.string.error)
            fireButton.isEnabled = false
        }
        if(args.isHost)
            setUpHostListeners()
        else
            setUpClientListeners()
        viewModel.winner.observe(main_activity) {
            if(it != "")
            {
                val tmp = it == "host"
                showInfo(if(!(tmp xor args.isHost)) R.string.you_won else R.string.you_lost)
                title.text = "End"
                fireButton.isEnabled = false
            }
        }
        return view
    }
    private fun layoutDragListener(v: View?, event: DragEvent?): Boolean
    {
        when (event!!.action) {
            DragEvent.ACTION_DROP -> {
                val shipView = (event.localState as ShipView)
                if (event.y - shipView.height > fireButton.y + 15) {
                    shipView.x = event.x - (shipView.width / 2)
                    shipView.y = event.y - (shipView.height / 2)
                    if (shipView.associatedFieldShip != null) {
                        viewModel.yourField!!.deleteShip(shipView.associatedFieldShip!!)
                        viewModel.placedShips[shipView.length - 1].value =
                            viewModel.placedShips[shipView.length - 1].value!! - 1
                        shipView.associatedFieldShip = null
                    }
                    return true
                } else {
                    if (rotateCheckBox.isChecked && shipView.associatedFieldShip!!.length > 1) {
                        shipView.rotation = if (shipView.isHorizontal) 90.0F else 0.0F
                        shipView.isHorizontal = !shipView.isHorizontal
                    }
                    return false
                }
            }
            else -> {
                return true
            }
        }
    }
    private fun recyclerDragListener(v: View?, event: DragEvent?): Boolean
    {
        val shipView = event!!.localState as ShipView
        when (event!!.action) {
            DragEvent.ACTION_DROP -> {
                val (start, end) = calculateFieldCoords(event.x, event.y, shipView)
                if (start.col < 0 || start.col > 9 || start.row < 0 || start.row > 9 ||
                    end.col < 0 || end.col > 9 || end.row < 0 || end.row > 9
                )
                    return false
                val ship = Ship(start, end)
                if (shipView.associatedFieldShip != null) {
                    viewModel.yourField!!.deleteShip(shipView.associatedFieldShip!!)
                }
                if (viewModel.yourField!!.isAllowedShip(ship)) {
                    viewModel.yourField!!.placeShip(ship)
                    if (ship.isHorizontal) {
                        shipView.x = yourRecycler.x + cell_width * start.col
                        shipView.y = yourRecycler.y + cell_width * start.row
                    } else {
                        shipView.x =
                            yourRecycler.x + cell_width * start.col - cell_width * (ship.length - 1) * 0.5F
                        shipView.y =
                            yourRecycler.y + cell_width * start.row + cell_width * (ship.length - 1) * 0.5F
                    }
                    if (shipView.associatedFieldShip == null)
                        viewModel.placedShips[ship.length - 1].value =
                            viewModel.placedShips[ship.length - 1].value!! + 1
                    shipView.associatedFieldShip = ship
                    return true
                } else {
                    if (shipView.associatedFieldShip != null)
                        viewModel.yourField!!.placeShip(shipView.associatedFieldShip!!)
                    if (rotateCheckBox.isChecked && shipView.length > 1) {
                        shipView.rotation = if (ship.isHorizontal) 90.0F else 0.0F
                        shipView.isHorizontal = !ship.isHorizontal
                    }

                }
                return false
            }
            else -> {
                return true
            }
        }
    }
    private fun showInfo(string_id: Int)
    {
        InfoDialogFragment(string_id)
            .show(main_activity.supportFragmentManager, "infoFragment")
    }
    private fun setUpHostListeners()
    {
        title.text = "ID: ${args.id}"
        readyButton.setOnClickListener {
            viewModel.hostReady.value = true
        }
        hostViewModel.clientConnected.observe(main_activity) {
            if(it)
                title.text = "Player connected"
        }
        hostViewModel.hostReady.observe(main_activity) {
            if(it)
            {
                viewModel.setReady()
                clearAfterShipPlacement()
                if(hostViewModel.clientReady.value!!)
                    hostViewModel.started.value = true
            }
        }
        hostViewModel.clientReady.observe(main_activity) {
            if(it && hostViewModel.hostReady.value!!)
                hostViewModel.started.value = true
        }
        hostViewModel.started.observe(main_activity) {
            if(it) {
                hostViewModel.onGameStarted()
                onGameStarted()
                viewModel.shotListener = this
                viewModel.isHostTurn.observe(main_activity){
                    title.text = if(it) "Your turn" else "Enemy's turn"
                    fireButton.isEnabled = it
                }
                fireButton.setOnClickListener {
                    val pos = enemyAdapter.selectedItemPos
                    if(pos != -1)
                        hostViewModel.processShot(Point(pos / 10, pos % 10), true)
                    enemyAdapter.makeSelection(pos)
                }
            }
        }
    }
    private fun setUpClientListeners()
    {
        title.text = "Place ships"
        readyButton.setOnClickListener {
            viewModel.clientReady.value = true
        }
        clientViewModel.clientReady.observe(main_activity) {
            if(it)
            {
                viewModel.setReady()
                clearAfterShipPlacement()
                clientViewModel.sendShipsToHost()
                if(!clientViewModel.hostReady.value!!)
                    title.text = "Wait for host"
            }
        }
        clientViewModel.started.observe(main_activity) {
            if(it) {
                clientViewModel.onGameStarted()
                onGameStarted()
                viewModel.shotListener = this
                viewModel.isHostTurn.observe(main_activity){
                    title.text = if(!it) "Your turn" else "Enemy's turn"
                    fireButton.isEnabled = !it
                }
                fireButton.setOnClickListener {
                    val pos = enemyAdapter.selectedItemPos
                    if(pos != -1)
                        clientViewModel.sendTurnToHost(Point(pos / 10, pos % 10))
                    enemyAdapter.makeSelection(pos)
                }
            }
        }
    }
    override fun onShot(point: Point, isHost: Boolean) {
        val adapter = if(args.isHost xor isHost) yourAdapter else enemyAdapter
        adapter.notifyItemChanged(point.row * 10 + point.col)
    }

    private fun onGameStarted()
    {
        title.text = "Started"
        fireButton.isEnabled = true
        enemyRecycler.visibility = RecyclerView.VISIBLE
        enemyAdapter = GameAdapter((viewModel.enemyField), true)
        enemyRecycler.adapter = enemyAdapter
    }
    private fun clearAfterShipPlacement()
    {
        for(ship in shipViews) {
            layout.removeView(ship)
            ship.visibility = View.GONE
        }
        layout.removeView(readyButton)
        readyButton.visibility = View.GONE
        layout.removeView(rotateCheckBox)
        rotateCheckBox.visibility = View.GONE
        yourAdapter.notifyDataSetChanged()
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
            if(rotateCheckBox.isChecked && ship.length > 1)
            {
                v.rotation = if(ship.isHorizontal) 90.0F else 0.0F
                ship.isHorizontal = !ship.isHorizontal
            }
            val myShadow = MyDragShadowBuilder(v)
            v.startDrag(null, myShadow, v, 0)
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
        shipViews = listOf(
            ship1_1,
            ship1_2,
            ship1_3,
            ship1_4,
            ship2_1,
            ship2_2,
            ship2_3,
            ship3_1,
            ship3_2,
            ship4_1
        )
        for(item in shipViews) {
            item.layoutParams.height = cell_width
            item.setOnLongClickListener(viewListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(args.isHost)
            hostViewModel.clear()
        else
            clientViewModel.clear()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        findNavController().navigateUp()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
    }
}

