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
        val manager = GridLayoutManager(requireContext(), 10)
        yourRecycler.layoutManager = manager
        yourRecycler.adapter = GameAdapter(Field());
        yourRecycler.minimumHeight = yourRecycler.width
        yourRecycler.setOnDragListener { v, event ->
            when (event!!.action) {
                DragEvent.ACTION_DROP -> {
                    // Displays a message containing the dragged data.
                    Toast.makeText(context, "${event.x} ${event.y}", Toast.LENGTH_SHORT).show()
                    val view = event.localState as View
                    view.x = event.x - (view.width / 2) + yourRecycler.x
                    view.y = event.y - (view.height / 2) + yourRecycler.y
                    true
                }
                else -> {
                    true
                }
            }
        }
        setUpShipPalette(view)
       /* imageview.apply {
            imageview.setOnLongClickListener { v: View ->
                if(rotate.isChecked)
                    v.rotation = 90.0F
                val myShadow = MyDragShadowBuilder(this)
                v.startDrag(
                        null,   // the data to be dragged
                        myShadow,   // the drag shadow builder
                        v,       // no need to use local data
                        0           // flags (not currently used, set to 0)
                )
            }
        }*/
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
        val palette = view.findViewById<LinearLayout>(R.id.game_ship_palette)
        palette.layoutParams.height = cell_width * 4
        for(item in palette.children)
            item.layoutParams.height = cell_width + 5
        val linears = palette.children.iterator()
        val first = (linears.next() as LinearLayout).children.iterator()
        val second = (linears.next() as LinearLayout).children.iterator()
        first.next().layoutParams.width = 4 * cell_width
        for(i in 1..4) {
            first.next().layoutParams.width = cell_width
            second.next().layoutParams.width = 2 * cell_width
        }
        val third = (linears.next() as LinearLayout).children.iterator()
        for(i in 1.. 2)
            third.next().layoutParams.width = 3 * cell_width
        for(linear in linears)
            for(item in (linear as LinearLayout).children)
                item.layoutParams.height = cell_width
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

