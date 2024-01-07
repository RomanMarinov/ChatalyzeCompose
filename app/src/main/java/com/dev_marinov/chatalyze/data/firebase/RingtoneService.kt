package com.dev_marinov.chatalyze.data.firebase

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


class RingtoneService : Service() {

    companion object {
        const val ACTION_PLAY_RINGTONE = "PLAY_RINGTONE"
        const val ACTION_STOP_RINGTONE = "STOP_RINGTONE"
    }

    private var ringtone: Ringtone? = null
    private val executor = ScheduledThreadPoolExecutor(1)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = try {
            RingtoneManager.getRingtone(applicationContext, notification)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_PLAY_RINGTONE -> {
                ringtone?.play()
                ringtone?.isLooping = true

                executor.schedule({
                    ringtone?.isLooping = false
                    ringtone?.stop()
                    stopSelf()
                }, 30, TimeUnit.SECONDS) // 30 seconds
            }
            ACTION_STOP_RINGTONE -> {
                ringtone?.isLooping = false
                ringtone?.stop()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
        executor.shutdownNow()
    }
}