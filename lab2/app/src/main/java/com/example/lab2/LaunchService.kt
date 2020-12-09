package com.example.lab2

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import com.example.lab2.db.*
import kotlinx.coroutines.*


class LaunchService: LifecycleService() {
    companion object {
        const val SERVICE_BROADCAST = "com.example.lab2.SERVICE_BROADCAST_ACTION"
    }
    private val binder: IBinder = MyBinder()
    val CHANNEL_ID = "Random number notification"
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var repository: IRepository
    public var got = false
    lateinit var seqInts: LiveData<SequenceWithIntervals>
    public lateinit var exploded : Array<Interval?>
    private lateinit var remainingTimes: List<Int>
    public var currentIdx = MutableLiveData<Int>().apply{ value = 0}
    public var remainingTime = MutableLiveData<Int>().apply {value = 0}
    public var currentIntervalRemainingTime = MutableLiveData<Pair<Short, Boolean>>().apply {value = Pair(0, true)}
    public var currentKind = MutableLiveData<Kind>().apply{ value = Kind.PREPARE}
    private var job: Job? = null
    public var isPaused = MutableLiveData<Boolean>().apply { value = true}
    public var isFinished = MutableLiveData<Boolean>().apply { value = true}
    public var size = MutableLiveData<Int>().apply{ value = 0}
    private var _phaseRemaining = 0

    private lateinit var kind_strings: Array<String>
    private lateinit var total_string: String
    private lateinit var phase_string: String
    private lateinit var sec_string: String
    private lateinit var reps_string: String
    private lateinit var start_string: String
    private lateinit var pause_string: String
    private lateinit var next_string: String
    private lateinit var prev_string: String
    private lateinit var notificationReceiver: BroadcastReceiver

    private lateinit var sectionPlayer: MediaPlayer
    private lateinit var finishPlayer: MediaPlayer
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(SERVICE_BROADCAST)
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra("timer")) {
                    "playpause" -> {
                        if(isPaused.value!!)
                        {
                            if(isFinished.value!!)
                                startTimer()
                            else
                                resumeTimer()
                        }
                        else
                            pauseTimer()
                    }
                    "prev" -> setPosition(if (currentIdx.value != 0) currentIdx.value!! - 1 else 0)
                    "next" -> setPosition(currentIdx.value!! + 1)
                }
            }
        }
        registerReceiver(notificationReceiver, filter)
        sectionPlayer = MediaPlayer.create(this, R.raw.change_interval)
        finishPlayer = MediaPlayer.create(this, R.raw.finish)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val res = super.onStartCommand(intent, flags, startId)
        repository = RoomRepository(application)
        kind_strings = application.resources.getStringArray(R.array.interval_types)
        total_string = application.resources.getString(R.string.total)
        phase_string = application.resources.getString(R.string.phase)
        sec_string = application.resources.getString(R.string.sec)
        reps_string = application.resources.getString(R.string.reps_short)
        next_string = application.resources.getString(R.string.next)
        prev_string = application.resources.getString(R.string.prev)
        start_string = application.resources.getString(R.string.start)
        pause_string = application.resources.getString(R.string.pause)
        notificationManager = NotificationManagerCompat.from(this)
        return res
    }
    public fun getSeqInts(id: Int): LiveData<SequenceWithIntervals>
    {
        seqInts = repository.getSeqInts(id)
        return seqInts
    }
    fun pauseTimer()
    {
        job?.cancel()
        job = null
        isPaused.value = true
    }
    fun setPosition(pos: Int)
    {
        job?.cancel()
        if(pos == exploded.size) {
            if(!(isPaused.value!!))
                finishPlayer.start()
            remainingTime.value = 0
            isPaused.value = true
            isFinished.value = true
        }
        else
        {
            if(!(isPaused.value!!))
                sectionPlayer.start()
            currentIdx.value = pos
            _phaseRemaining = exploded[pos]!!.time.toInt()
            currentIntervalRemainingTime.value = Pair(exploded[pos]!!.time, exploded[pos]!!.isSeconds.value!!)
            remainingTime.value = remainingTimes[pos]
        }
        if(!isPaused.value!!)
            _internalTimer()
    }
    fun resumeTimer()
    {
        isPaused.value = false
        _internalTimer()
    }
    fun resumeFromReps()
    {
        if(currentIdx.value!! + 1 != exploded.size) {
            currentIdx.value = currentIdx.value!! + 1
            _phaseRemaining = exploded[currentIdx.value!!]!!.time.toInt()
            _internalTimer()
        }
        else
        {
            isPaused.value = true
            isFinished.value = true
            finishPlayer.start()
        }
    }
    private fun _internalTimer()
    {
        job = lifecycleScope.launch(Dispatchers.Main) {
            while(true)
            {
                val interval = exploded[currentIdx.value!!]!!
                currentKind.value = interval.kind.value
                if(interval.isSeconds.value!!)
                    for(i in _phaseRemaining downTo 1)
                    {
                        _phaseRemaining = i
                        currentIntervalRemainingTime.value = Pair(i.toShort(), interval.isSeconds.value!!)
                        delay(1000)
                        remainingTime.value = remainingTime.value!! - 1
                    }
                else
                {
                    currentIntervalRemainingTime.value = Pair(interval.time, interval.isSeconds.value!!)
                    return@launch
                }
                if(currentIdx.value!! + 1 == exploded.size)
                {
                    finishPlayer.start()
                    break
                }
                else
                {
                    currentIdx.setValue(currentIdx.value!! + 1)
                    sectionPlayer.start()
                }
                _phaseRemaining = exploded[currentIdx.value!!]!!.time.toInt()
            }
            isPaused.value = true
            isFinished.setValue(true)
        }
    }
    fun startTimer()
    {
        isPaused.value = false
        isFinished.value = false
        setPosition(0)
    }
    fun explodeInts(): Array<Interval?> {
        val ints = seqInts.value!!.intervals
        ints.sortBy { it.pos.value!! }
        var repeats_kind_count = 0
        var repeats_total = 0
        for (int in ints) {
            if (int.kind.value!! == Kind.REPEAT) {
                repeats_kind_count += 1
                repeats_total += int.time - 1
            }
        }
        val repetition_size = (ints.size - repeats_kind_count + 2 * repeats_total)
        exploded = Array<Interval?>(repetition_size * seqInts.value!!.seq.repetitions) { null }
        var insertIdx = 0
        for (idx in 0 until ints.size) {
            if (ints[idx].kind.value!! == Kind.REPEAT) {
                for (i in 1 until ints[idx].time) {
                    for (j in 2 downTo 1) {
                        val newInt = ints[idx - j].copy()
                        newInt.pos.value = (insertIdx + 1).toByte()
                        exploded[insertIdx++] = newInt
                    }
                }
            } else {
                val newInt = ints[idx].copy()
                newInt.pos.value = (insertIdx + 1).toByte()
                exploded[insertIdx++] = newInt
            }
        }
        for (i in 1 until seqInts.value!!.seq.repetitions) {
            for (j in 0 until repetition_size) {
                val newInt = exploded[j]!!.copy()
                newInt.pos.value = (insertIdx + 1).toByte()
                exploded[insertIdx++] = newInt
            }
        }
        var cumsum = 0
        remainingTimes = exploded.reversed().map {
            if (it!!.isSeconds.value!!)
                cumsum += it!!.time
            cumsum
        }.reversed()
        size.value = exploded.size
        remainingTime.value = remainingTimes[0]
        currentIntervalRemainingTime.value = Pair(exploded!![0]!!.time, exploded!![0]!!.isSeconds.value!!)
        return exploded
    }
    inner class MyBinder : Binder() {
        val service: LaunchService
            get() = this@LaunchService
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return binder
    }

    private fun getNotification(): Notification {
        val interval = exploded[currentIdx.value!!]!!
        val builder =  NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$total_string %02d:%02d:%02d; $phase_string ${currentIdx.value!! + 1}/${exploded.size}".format(
                remainingTime.value!! / 3600,
                remainingTime.value!! / 60 % 60,
                remainingTime.value!! % 60
            ))
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
        if(!(isFinished.value!!)){
            builder.setContentText(
                "${kind_strings[interval.kind.value!!.ordinal]} ${interval.time} ${if(interval.isSeconds.value!!) sec_string else reps_string}"
            ).setProgress(interval.time.toInt(), interval.time.toInt() - _phaseRemaining, !(interval.isSeconds.value!!))
        }
        var intent = Intent(SERVICE_BROADCAST)
        intent.putExtra("timer", "prev")
        var pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.addAction(R.drawable.ic_baseline_skip_previous_24, prev_string, pendingIntent)
        intent = Intent(SERVICE_BROADCAST)
        intent.putExtra("timer", "next")
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.addAction(R.drawable.ic_baseline_skip_next_24, next_string, pendingIntent)
        intent = Intent(SERVICE_BROADCAST)
        intent.putExtra("timer", "playpause")
        pendingIntent = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if(isPaused.value!!)
            builder.addAction(R.drawable.ic_baseline_play_arrow_white_24, start_string, pendingIntent)
        else
            builder.addAction(R.drawable.ic_baseline_pause_white_36, pause_string, pendingIntent)
        return builder.build()
    }
    public fun startNotification() {
        currentIntervalRemainingTime.observe(this) {
            updateNotification()
        }
        currentIdx.observe(this){
            updateNotification()
        }
        isFinished.observe(this){
            updateNotification()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification: Notification = getNotification()
        startForeground(1, notification)
    }

    private fun updateNotification()
    {
        notificationManager.notify(1, getNotification())
    }
}