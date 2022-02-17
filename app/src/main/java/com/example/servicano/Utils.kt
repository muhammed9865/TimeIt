package com.example.servicano

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log

fun Activity.isServiceActive(serviceName: String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(Int.MAX_VALUE)){
        if (serviceName == service.service.className){
            Log.d("ServiceActive", "isServiceActive: true")
            return true
        }
    }
    return false
}