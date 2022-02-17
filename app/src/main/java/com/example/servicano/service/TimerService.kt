package com.example.servicano.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.servicano.Constants
import com.example.servicano.R
import java.util.*

class TimerService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: NotificationCompat.Builder
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(NotificationManager::class.java)
        val time = intent!!.getIntExtra(TIMER_CURRENT, 0)
        timer.scheduleAtFixedRate(TimerTask(time), 0, TIMER_TICK)
        notification = getNotification(this)
        if (Build.VERSION.SDK_INT >= 26) {
            if (isForeground) {
                notificationManager.createNotificationChannel(getNotificationChannel())
                startForeground(Constants.TIMER_SERVICE_ID, notification.build())
            }
        }

        startForeground(Constants.TIMER_SERVICE_ID, notification.build())
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            Constants.TIMER_NOTIFICATION_CHANNELID,
            Constants.TIMER_NOTIFICATION_CHANNELID,
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    private fun getNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Constants.TIMER_NOTIFICATION_CHANNELID)
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOnlyAlertOnce(true)

    }

    private inner class TimerTask(private var time: Int) : java.util.TimerTask() {
        private val intent = Intent(TIMER_DURATION)
        init {
            instances++
        }
        override fun run() {
            Log.d("service", "run: $instances")
            if (time >= 0 && isActive) {
                if (isForeground) {
                    notification
                        .setContentText(getTimeAsText(time))

                    notificationManager.notify(Constants.TIMER_SERVICE_ID, notification.build())
                }
                time--
                intent.putExtra(TIMER_CURRENT, time)
                sendBroadcast(intent)
            } else {

                if (time <= 0) {
                    Log.e("service", "run: false")
                    stopSelf()
                    cancel()
                    timer.cancel()
                    timer.purge()
                }
            }
        }

    }

    private fun getTimeAsText(time: Int): String {
        val minutes = time.floorDiv(60)
        val seconds = time % 60

        return "$minutes : $seconds"
    }

    companion object {
        var isActive = true
        var isForeground = false
        const val TIMER_DURATION = "TimerDuration"
        const val TIMER_CURRENT = "TimerCurrent"
        private const val TIMER_TICK = 1000L
        private var instances = 0
    }
}