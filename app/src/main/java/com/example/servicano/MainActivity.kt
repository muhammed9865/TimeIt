package com.example.servicano

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.core.widget.doOnTextChanged
import com.example.servicano.databinding.ActivityMainBinding
import com.example.servicano.service.TimerService
import com.example.servicano.viewmodel.TimerViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: TimerViewModel by lazy {
        ViewModelProvider(this)[TimerViewModel::class.java]
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var timerServiceIntent: Intent

    override fun onStart() {
        super.onStart()
        binding.activity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        TimerService.isForeground = false
        // Listeners for edit texts changes
        onMinutesTextChanged()
        onSecondsTextChanged()

        // Set the text of minutes and seconds onCreate

        viewModel.apply {
            if (getSecondsAsText() != "00") {
                binding.minutesOffsetTv.setText(viewModel.getSecondsAsText())
            }

            if (getMinutesAsText() != "00") {
                binding.secondsOffsetTv.setText(viewModel.getSecondsAsText())
            }
        }


        // Used to create the Timer Service to control it
        timerServiceIntent = Intent(this.applicationContext, TimerService::class.java)

        binding.startTimerBtn.setOnClickListener {
            if (viewModel.getTime() > 1)
                startTimer()
        }

        if (!isServiceActive(timerServiceIntent::class.java.name))
            registerReceiver(onTimerUpdated, IntentFilter(TimerService.TIMER_DURATION))

    }

    private val onTimerUpdated = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent) {
            val time = intent.getIntExtra(TimerService.TIMER_CURRENT, 0)
            viewModel.getTimeFromInt(time)
            updateTexts()
            Log.d("time", "onReceive: $time")
            if (time == 0) {
                stopTimer()
            }
        }
    }

    private fun onMinutesTextChanged() {
        binding.minutesOffsetTv.apply {
            doOnTextChanged { text, _, _, _ ->
                viewModel.setMinutes(text.toString())
            }
        }

        // On Minutes Increment clicked
        binding.minutesIncrement.setOnClickListener {

            viewModel.apply {
                incrementMinutes()
                binding.minutesOffsetTv.setText(getMinutesAsText())
            }
        }

        // On Minutes Decrement clicked
        binding.minutesDecrement.setOnClickListener {
            viewModel.apply {
                decrementMinutes()
                binding.minutesOffsetTv.setText(getMinutesAsText())
            }
        }
    }

    private fun onSecondsTextChanged() {
        binding.secondsOffsetTv.apply {
            doOnTextChanged { text, _, _, _ ->
                viewModel.setSeconds(text.toString())
            }
        }

        // On Seconds Increment clicked
        binding.secondsIncrement.setOnClickListener {
            viewModel.apply {
                incrementSeconds()
                binding.secondsOffsetTv.setText(getSecondsAsText())
            }

        }

        // On Seconds Decrement clicked
        binding.secondsDecrement.setOnClickListener {
            viewModel.apply {
                decrementSeconds()
                binding.secondsOffsetTv.setText(getSecondsAsText())
            }
        }
    }

    fun startTimer() {
        val time = viewModel.getTime()
        if (!isServiceActive(TimerService::class.java.name)) {
            Log.d(TAG, "newTimer: $time")
            stopService(timerServiceIntent)
            registerReceiver(onTimerUpdated, IntentFilter(TimerService.TIMER_DURATION))
            timerServiceIntent.putExtra(TimerService.TIMER_CURRENT, time)
            timerServiceStart(timerServiceIntent)
            binding.timerSeekbar.max = time
        } else {
            Log.d(TAG, "newTimer: false")
            timerServiceIntent.putExtra(TimerService.TIMER_CURRENT, time)
            TimerService.isActive = true
        }
    }

    fun pauseTimer() {
        TimerService.isActive = false
    }

    fun stopTimer() {
        stopService(timerServiceIntent)
        unregisterReceiver(onTimerUpdated)
    }

    private fun updateTexts() {
        binding.apply {
            viewModel.apply {
                minutesOffsetTv.setText(getMinutesAsText())
                secondsOffsetTv.setText(getSecondsAsText())
                timerSeekbar.progress = getTime()
            }
        }
    }

    private fun timerServiceStart(intent: Intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }


    override fun onDestroy() {
        if (viewModel.getTime() > 0) {
            TimerService.isForeground = true
        } else {
            stopService(timerServiceIntent)
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}