package com.dev_marinov.chatalyze.data.firebase

import android.app.KeyguardManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.MainScreensActivity
import com.dev_marinov.chatalyze.presentation.util.Constants
import javax.inject.Singleton


@Singleton
open class BroadcastReceiverNotification() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {

            when(intent?.action) {
                "full_screen" -> {

                    val res = intent.getStringExtra("channelID")?.toInt()
                    // есть ли у приложения разрешение на отображение наложений поверх других приложений
                    val hasOverlayPermission = Settings.canDrawOverlays(context)

                    val keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    val isScreenLocked = keyguardManager.isDeviceLocked // позволяет получить информацию о состоянии блокировки экрана

                    val senderPhone = intent.getStringExtra("senderPhone")
                    val recipientPhone = intent.getStringExtra("recipientPhone")
                    val typeEvent = Constants.INCOMING_CALL_EVENT
                    val deepLink = Uri.parse("scheme_chatalyze://call_screen/$senderPhone/$recipientPhone/$senderPhone/$typeEvent")
                    val deepLinkIntent = Intent(
                        Intent.ACTION_VIEW,
                        deepLink,
                        context,
                        MainScreensActivity::class.java
                    )

                    val resultPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
                        addNextIntentWithParentStack(deepLinkIntent)
                        getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)
                    }
                    resultPendingIntent.send()
                }
                "click_notification_missed_call" -> {
                    Log.d("4444", " сработал click_notification_missed_call")
                }

                "missed_call_notification_swipe" -> {

                }
                "call_notification_swipe" -> {
                    ringtoneStop(context = context)
                }
            }
        } catch (e: Exception) {
            Log.d("4444", " try catch BroadcastReceiverNotification e=" + e)
        }

        if (intent?.action == "kill_screen") {
            Log.d("4444", " intent?.action == kill_screen")
            if (context == null) {
                Log.d("4444", " kill_screen context NULL")
            } else {
                Log.d("4444", " kill_screen context NOT NULL")
            }

            val killIntent = Intent("close_call_activity")
            killIntent.putExtra("close_activity", true)
            context?.let { ctx ->
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(killIntent)
            }
        }
    }

    private fun ringtoneStop(context: Context?) {
        try {
            val intent = Intent(context, RingtoneService::class.java)
            intent.action = RingtoneService.ACTION_STOP_RINGTONE
            context?.startService(intent)
        } catch (e: Exception) {
            Log.d("4444", " try catch Ошибка остановки звука звонка: ", e)
        }
    }

    private fun deleteNotification(context: Context, channelId: Int?) {
        Log.d("4444", " deleteNotification channelId=" + channelId)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        channelId?.let {
            notificationManager.cancel(it)
        }
    }
}