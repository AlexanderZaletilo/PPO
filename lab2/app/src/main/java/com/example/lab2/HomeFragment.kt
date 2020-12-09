package com.example.lab2

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBar
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.adapters.SequenceAdapter
import com.example.lab2.db.Interval
import com.example.lab2.db.SequenceWithIntervals
import com.example.lab2.viewmodels.ListSequencesViewModel
import com.example.lab2.viewmodels.SequenceViewModel
import com.example.lab2.viewmodels.SomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class HomeFragment : Fragment(), SequenceAdapter.ListItemClickListener {
    private lateinit var viewModel: ListSequencesViewModel
    private lateinit var seqViewModel: SequenceViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var bar: ActionBar
    private lateinit var main_activity: MainActivity
    private lateinit var adapter: SequenceAdapter
    override fun onAttach(context: Context) {
        super.onAttach(context)
        main_activity = context as MainActivity
        bar = main_activity.supportActionBar!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel =
            ViewModelProvider(this, SomeViewModelFactory.getInstance(main_activity.application)).get(
                ListSequencesViewModel::class.java
            )
        setHasOptionsMenu(true)
        recycler = view.findViewById(R.id.seq_recycler)
        recycler.layoutManager = LinearLayoutManager(main_activity)
        seqViewModel = ViewModelProvider(main_activity).get(SequenceViewModel::class.java)
        if (seqViewModel.fromEditor) {
            seqViewModel.fromEditor = false
            if (seqViewModel.created)
            {
                seqViewModel.created = false
                viewModel.insert(seqViewModel.seqInts)
            }
            else if (seqViewModel.changed) {
                seqViewModel.changed = false
                viewModel.update(seqViewModel.seqInts, seqViewModel.deleted)
            }
        }
        if (viewModel.got)
            finishCreation()
        else
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.fetch()
            }.invokeOnCompletion {
                finishCreation()
            }
        return view
    }
    private fun finishCreation()
    {
        viewModel.sort()
        adapter = SequenceAdapter(main_activity,  viewModel.seqsInts,  this)
        setToolbarColor()
        recycler.adapter = adapter
    }
    private fun getToolbarColor(): Int
    {
        return if(viewModel.seqsInts.size > 0)
            viewModel.seqsInts[0].seq.color.value!!
        else
            Color.RED
    }
    private fun setToolbarColor()
    {
        bar.setBackgroundDrawable(ColorDrawable(getToolbarColor()))
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_sequence_list_menu, menu)
        super.onCreateOptionsMenu(menu!!, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_seq_add -> {
                view?.let {
                    val newSeq = com.example.lab2.db.Sequence()
                    val ints = mutableListOf<Interval>()
                    val new = SequenceWithIntervals(newSeq, ints)
                    seqViewModel.initSequence(new)
                    seqViewModel.fromEditor = false
                    seqViewModel.created = true
                    it.findNavController().navigate(HomeFragmentDirections.toSequence())
                }
            }
            R.id.action_settings -> {
                view?.let {
                    startActivity(Intent(context, SettingsActivity::class.java))
                    main_activity.finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onListItemClick(position: Int, holder: SequenceAdapter.SequenceViewHolder) {
        val seqInts: SequenceWithIntervals = adapter.list[position]
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.MyPopup), holder.actionsButton)
        popupMenu.menuInflater.inflate(R.menu.sequence_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_seq_edit ->{
                    seqViewModel.initSequence(seqInts)
                    viewModel.editingIdx = position
                    requireView().findNavController().navigate(HomeFragmentDirections.toSequence())
                }
                R.id.action_seq_delete -> {
                    viewModel.delete(seqInts, position)
                    adapter.notifyDataSetChanged()
                    setToolbarColor()
                }
                R.id.action_seq_launch -> {
                    if(seqInts.intervals.size < 1)
                        InfoDialogFragment(R.string.zero_intervals).show(
                            main_activity.supportFragmentManager,
                            "infoDialogFragment"
                        )
                    else {
                        val intent = Intent(main_activity, LaunchActivity::class.java)
                        intent.putExtra("SEQUENCE_ID", seqInts.seq.id.toInt())
                        main_activity.startActivity(intent)
                    }
                }
            }
            true
        })
        popupMenu.show()
    }
}

