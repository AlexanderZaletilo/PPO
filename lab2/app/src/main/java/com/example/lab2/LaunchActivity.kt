package com.example.lab2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.adapters.IntervalLaunchAdapter
import com.example.lab2.db.Interval
import com.example.lab2.viewmodels.LaunchViewModel


class LaunchActivity : BaseActivity() {
    private lateinit var viewModel: LaunchViewModel
    private lateinit var recycler: RecyclerView
    private var fontId: Int? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            val binder = iBinder as LaunchService.MyBinder
            viewModel.service = binder.service
            viewModel.isBound = true
            this@LaunchActivity.bindViews()
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            viewModel.isBound = false
        }
    }
    private fun bindViews()
    {
        val linearManager = LinearLayoutManager(this)
        recycler.layoutManager = linearManager
        val service = viewModel.service!!
        val adapter = IntervalLaunchAdapter(this, service)
        recycler.adapter = adapter
        if (service.got) {
            adapter.setData(service.exploded.toList() as List<Interval>)
            setUpColor()
            service.startNotification()

        }
        else
            service.getSeqInts(intent.extras!!.getInt("SEQUENCE_ID")).observe(this)
            {
                if(it != null)
                {
                    service.got = true
                    adapter.setData(service.explodeInts().toList() as List<Interval>)
                    setUpColor()
                    service.startNotification()
                }
            }
        val playButton = findViewById<ImageButton>(R.id.launch_button_play_pause)
        playButton.setOnClickListener {
            if(service.isPaused.value!!)
            {
                if(service.isFinished.value!!)
                    service.startTimer()
                else
                    service.resumeTimer()
            }
            else
                service.pauseTimer()
        }
        val phase_type_textview = findViewById<TextView>(R.id.launch_textview_phase_type)
        val timer_textview = findViewById<TextView>(R.id.launch_textview_phase_time)
        val remaining_textview = findViewById<TextView>(R.id.launch_textview_phase_remaining)
        val position_textview = findViewById<TextView>(R.id.launch_textView_phase_count)
        val prev_button = findViewById<ImageButton>(R.id.launch_button_skip_prev)
        val next_button = findViewById<ImageButton>(R.id.launch_button_skip_next)
        val kind_strings = resources.getStringArray(R.array.interval_types)
        service.currentKind.observe(this){
            phase_type_textview.text = kind_strings[it.ordinal]
        }
        service.isPaused.observe(this){
            playButton.isSelected = !it
        }
        val reps_string = application.resources.getString(R.string.reps_short)
        service.currentIntervalRemainingTime.observe(this){
            if(!it.second)
            {
                val ss = SpannableString("${it.first} $reps_string")
                val len = it.first.toString().length
                ss.setSpan(RelativeSizeSpan(1f), 0, len, 0)
                ss.setSpan(RelativeSizeSpan(0.35f), len + 1, len + 5, 0)
                remaining_textview.text = ss
            }
            else
                remaining_textview.text = it.first.toString()
        }
        service.isFinished.observe(this){
            remaining_textview.text = "0!"
        }
        service.remainingTime.observe(this){
            timer_textview.text = "%02d:%02d:%02d".format(
                it / 3600, it / 60 % 60, it % 60
            )
        }
        remaining_textview.setOnClickListener {
            if(!service.isPaused.value!! && !service.currentIntervalRemainingTime.value!!.second)
                service.resumeFromReps()
        }
        service.size.observe(this){ it_size ->
            service.currentIdx.observe(this){ it_idx ->
                position_textview.text = "${it_idx + 1}/${it_size}"
            }
        }
        service.currentIdx.observe(this){
            adapter.makeSelection(it)
            recycler.post {
                if(it <= linearManager.findLastVisibleItemPosition() - 1)
                    recycler.smoothScrollToPosition(it + 2)
                else
                    recycler.smoothScrollToPosition(it)
            }
        }
        prev_button.setOnClickListener {
            service.setPosition(if (service.currentIdx.value != 0) service.currentIdx.value!! - 1 else 0)
        }
        next_button.setOnClickListener {
            service.setPosition(service.currentIdx.value!! + 1)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        withActionBar = false
        super.onCreate(savedInstanceState)
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        fontId = when(preference.getString("font", "medium"))
        {
            "small" -> R.style.FontStyle_Small
            "large" -> R.style.FontStyle_Large
            else -> R.style.FontStyle_Medium
        }
        this.theme?.applyStyle(fontId!!, true);
        setContentView(R.layout.activity_launch)
        viewModel = ViewModelProvider(this).get(LaunchViewModel::class.java)
        recycler = findViewById<RecyclerView>(R.id.recycler_phases)
        bindService()
    }

    private fun bindService() {
        val intent = Intent(this, LaunchService::class.java)
        if(viewModel.service == null)
            startService(intent)
        bindService(intent, serviceConnection, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        if(isFinishing)
            stopService(Intent(this, LaunchService::class.java))
    }
    private fun setUpColor()
    {
        if(!isDark!!)
        {
            val layout = findViewById<ConstraintLayout>(R.id.launch_layout)
            layout.setBackgroundColor(viewModel.service!!.seqInts.value!!.seq.color.value!!)
        }
    }
}