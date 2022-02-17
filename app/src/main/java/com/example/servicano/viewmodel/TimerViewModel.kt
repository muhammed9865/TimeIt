package com.example.servicano.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {
    private val _minutes = MutableLiveData<Int>(0)
    val minutes: LiveData<Int> = _minutes
    private val _seconds = MutableLiveData<Int>(0)
    val seconds: LiveData<Int> = _seconds

    fun setMinutes(minutes: String) {
        _minutes.value = if (minutes >= 0.toString()) minutes.toInt() else 0
    }

    fun setSeconds(seconds: String) {
        _seconds.value = if (seconds >= 0.toString()) seconds.toInt() else 0
    }

    fun getMinutesAsText(): String {
        return if (_minutes.value!! > -1) {
            if (_minutes.value!! < 10) {
                "0${_minutes.value}"
            } else {
                _minutes.value.toString()
            }
        } else "00"
    }

    fun getSecondsAsText(): String {
        return if (_seconds.value!! > -1) {
            if (_seconds.value!! < 10) {
                "0${_seconds.value}"
            } else {
                _seconds.value.toString()
            }
        } else "00"
    }

    fun incrementMinutes() {
        setMinutes(_minutes.value?.plus(1).toString())
    }

    fun decrementMinutes() {
        setMinutes(_minutes.value?.minus(1).toString())
    }

    fun incrementSeconds() {
        setSeconds(_seconds.value?.plus(1).toString())
    }

    fun decrementSeconds() {
        setSeconds(_seconds.value?.minus(1).toString())
    }

    fun getTime(): Int {
        Log.d("time", "getTime: ${_minutes.value}:${_seconds.value}")
        return ((_minutes.value?.times(60))?.plus(_seconds.value!!))!!
    }

    fun getTimeFromInt(time: Int) {
        _minutes.value = time.floorDiv(60)
        _seconds.value = time % 60

    }

}