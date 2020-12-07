package com.example.lab2

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.adapters.IntervalAdapter
import com.example.lab2.db.Interval
import com.example.lab2.db.Kind
import com.example.lab2.viewmodels.SequenceViewModel

class SequenceFragment : Fragment(), SaveDialogFragment.SaveDialogListener,
        ColorPaletteDialogFragment.ColorDialogListener,
        IntervalAdapter.OnClickListener
{
    private lateinit var recycler: RecyclerView
    private lateinit var viewModel: SequenceViewModel
    private lateinit var bar: ActionBar
    private lateinit var main_activity: MainActivity
    private lateinit var adapter: IntervalAdapter
    override fun onAttach(context: Context) {
        super.onAttach(context)
        main_activity = context as MainActivity
        bar = main_activity.supportActionBar!!
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = this
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                viewModel.fromEditor = true
                val eq = viewModel.seqInts.equals(viewModel.originalSeqInts)
                viewModel.changed = !eq
                if(viewModel.created)
                {
                    findNavController().navigateUp()
                    return@handleOnBackPressed
                }
                if(eq)
                    findNavController().navigateUp()
                else
                    SaveDialogFragment(fragment as SaveDialogFragment.SaveDialogListener)
                        .show(main_activity.supportFragmentManager, "saveDialogFragment")
            }
        }
        main_activity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_sequence_menu, menu)
        super.onCreateOptionsMenu(menu!!, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_interval_add -> {
                addLast()
            }
            R.id.action_seq_edit_done -> {
                viewModel.changed = true
                viewModel.fromEditor = true
                findNavController().navigateUp()
            }
            R.id.action_seq_edit_palette -> {
                ColorPaletteDialogFragment(this, viewModel.seqInts.seq.color.value!!)
                    .show(main_activity.supportFragmentManager, "colorDialogFragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun addLast()
    {
        val int = Interval((recycler.adapter!!.itemCount + 1).toByte())
        viewModel.seqInts.intervals.add(int)
        (recycler.adapter as IntervalAdapter).notifyItemInserted(recycler.adapter!!.itemCount - 1)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(main_activity).get(SequenceViewModel::class.java)
        val seqInts = viewModel.seqInts
        val view = inflater.inflate(R.layout.fragment_sequence, container, false)
        setHasOptionsMenu(true)
        val title: EditText = view.findViewById(R.id.sequence_editTitle)
        title.setText(seqInts.seq.title)
        val reps: EditText = view.findViewById(R.id.sequence_editReps)
        viewModel.seqInts.seq.color.observe(viewLifecycleOwner) {
            bar.setBackgroundDrawable(ColorDrawable(it!!))
        }
        reps.setText(seqInts.seq.repetitions.toString())
        reps.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && (reps.text.isEmpty() || (reps.text.toString() == "0")))
            {
                seqInts.seq.repetitions = 1
                reps.text.clear()
                reps.text.insert(0, "1")
            }
        }
        title.doOnTextChanged { text, _, _, _ ->  seqInts.seq.title = text.toString()}
        reps.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty())
                seqInts.seq.repetitions = text.toString().toByte()}
        adapter = IntervalAdapter(seqInts.intervals, main_activity, this)
        recycler = view.findViewById<RecyclerView>(R.id.sequence_intervals_recycler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireActivity())
        return view
    }
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        viewModel.changed = true
        findNavController().navigateUp()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        viewModel.changed = false
        findNavController().navigateUp()
    }

    override fun onDialogNeutralClick(dialog: DialogFragment) {
        return
    }

    override fun onColorSelected(selected: Int) {
        viewModel.seqInts.seq.color.value = selected
    }
    private fun checkWorkRepeat(interval: Interval, pos: Int) = interval.kind.value == Kind.WORK &&
            adapter.list.size > pos + 2 && adapter.list[pos + 2].kind.value == Kind.REPEAT

    private fun checkRestRepeat(interval: Interval, pos: Int) = interval.kind.value == Kind.REST &&
            adapter.list.size > pos + 1 && adapter.list[pos + 1].kind.value == Kind.REPEAT

    override fun onClick(pos: Int, holder: IntervalAdapter.IntervalViewHolder) {
        val interval = adapter.list[pos]
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.MyPopup), holder.menu)
        popupMenu.menuInflater.inflate(R.menu.interval_type_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            val position = interval.pos.value!! - 1
            when(item.itemId) {
                R.id.action_interval_delete ->{
                    if(checkWorkRepeat(interval, pos) || checkRestRepeat(interval, pos)) {
                        InfoDialogFragment(R.string.work_rest_info).show(
                            main_activity.supportFragmentManager,
                            "infoDialogFragment"
                        )
                        return@OnMenuItemClickListener true
                    }
                    for(i in position + 1 until adapter.itemCount) {
                        adapter.list[i].pos.value = (adapter.list[i].pos.value!! - 1).toByte()
                    }
                    val deletedId = adapter.list[position].intervalId
                    if(deletedId != 0)
                        viewModel.deleted.add(deletedId)
                    adapter.list.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    return@OnMenuItemClickListener true
                }
                R.id.action_interval_add_above -> {
                    if(interval.kind.value == Kind.REPEAT || checkRestRepeat(interval, pos)) {
                        InfoDialogFragment(R.string.work_rest_info).show(
                            main_activity.supportFragmentManager,
                            "infoDialogFragment"
                        )
                        return@OnMenuItemClickListener true}
                    val int: Interval = Interval((position + 1).toByte())
                    adapter.list.add(position, int)
                    for(i in position + 1 until adapter.itemCount) {
                        adapter.list[i].pos.value = (adapter.list[i].pos.value?.plus(1))?.toByte()
                    }
                    adapter.notifyItemInserted(position)
                    return@OnMenuItemClickListener true
                }
                R.id.action_interval_add_below -> {
                    if(checkWorkRepeat(interval, pos) || checkRestRepeat(interval, pos)) {
                        InfoDialogFragment(R.string.work_rest_info).show(
                            main_activity.supportFragmentManager,
                            "infoDialogFragment"
                        )
                        return@OnMenuItemClickListener true}
                    val int: Interval = Interval((position + 2).toByte())
                    adapter.list.add(position + 1, int)
                    for(i in position + 2 until adapter.itemCount) {
                        adapter.list[i].pos.value = (adapter.list[i].pos.value?.plus(1))?.toByte()
                    }
                    adapter.notifyItemInserted(position + 1)
                    return@OnMenuItemClickListener true
                }
            }
            if(item.itemId == R.id.action_interval_repeat)
            {
                if(position < 2 || !((adapter.list[position - 2].kind.value == Kind.WORK &&
                            adapter.list[position - 1].kind.value == Kind.REST)))
                    InfoDialogFragment(R.string.repeat_info).show(main_activity.supportFragmentManager, "infoDialogFragment")
                else {
                    interval.kind.value = Kind.REPEAT
                    interval.isSeconds.value = false
                }
                return@OnMenuItemClickListener true
            }
            if(interval.kind.value == Kind.WORK && item.itemId != R.id.action_interval_work)
            {
                if(adapter.list.size > pos + 2 && adapter.list[pos + 2].kind.value == Kind.REPEAT) {
                    InfoDialogFragment(R.string.work_rest_info).show(
                        (context as MainActivity).supportFragmentManager,
                        "infoDialogFragment"
                    )
                    return@OnMenuItemClickListener true
                }
            }
            if(interval.kind.value == Kind.REST && item.itemId != R.id.action_interval_rest)
            {
                if(adapter.list.size > pos + 1 && adapter.list[pos + 1].kind.value == Kind.REPEAT){
                    InfoDialogFragment(R.string.work_rest_info).show(main_activity.supportFragmentManager, "infoDialogFragment")
                    return@OnMenuItemClickListener true
                }
            }
            interval.kind.value =  when(item.itemId) {
                R.id.action_interval_prepare ->
                    Kind.PREPARE
                R.id.action_interval_rest ->
                    Kind.REST
                R.id.action_interval_work ->
                    Kind.WORK
                R.id.action_interval_cooldown ->
                    Kind.COOL_DOWN
                else -> Kind.PREPARE
            }
            true
        })
        popupMenu.show()
    }
}