package com.dev_marinov.chatalyze.data.firebase

import android.app.KeyguardManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import javax.inject.Singleton


@Singleton
open class BroadcastReceiverNotification() : BroadcastReceiver() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        try {

            when(intent?.action) {
                "full_screen" -> {
                    Log.d("4444", " intent?.action == full_screen")

                    val res = intent.getStringExtra("channelID")?.toInt()
                    Log.d("4444", " full_screen res=" + res)

                    // есть ли у приложения разрешение на отображение наложений поверх других приложений
                    val hasOverlayPermission = Settings.canDrawOverlays(context)

                    val keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    val isScreenLocked = keyguardManager.isDeviceLocked // позволяет получить информацию о состоянии блокировки экрана
                    Log.d("4444", " BroadcastReceiver Notification hasOverlayPermission=" + hasOverlayPermission  + " isScreenLocked=" + isScreenLocked)

                    val recipient = "9303454564" // Замените этим реальным номером получателя
                    val sender = "5551234567" // Замените этим вашим реальным номером
                    val typeEvent = Constants.INCOMING_CALL_EVENT

// Правильный формат с заменой переменных
//                    val deepLink = Uri.parse("chatalyze://call_screen/$recipient/$sender/$typeEvent")
                    //val deepLink = Uri.parse("chatalyze://call_screen/{${"RECIPIENT_NAME"}}/{${recipient}}/{${sender}}/{${typeEvent}}")
//                    val deepLink = listOf(navDeepLink {
//                        uriPattern =
//                            "chatalyze://${ScreenRoute.CallScreen.route}/{$recipient}/{$sender}/{$typeEvent}"
//                    })

//                    val deepLink = Uri.parse("chatalyze://${ScreenRoute.CallScreen.route}/{$recipient}/{$sender}/{$typeEvent}")

                   // val uri = "chatalyze://${ScreenRoute.CallScreen.route}/${recipient}/${sender}/${typeEvent}".toUri()


                    //проверить эту ебаную ссылку просто повесить клик на любую кноку и че будет
//                    val uri = "scheme_chatalyze://${ScreenRoute.CallScreen.route}/${"roma"}${recipient}/${sender}/${typeEvent}".toUri()
                    val uri = "scheme_chatalyze://call_screen/roma/$recipient/$sender/$typeEvent".toUri()
                    val deepLink = Intent(Intent.ACTION_VIEW, uri)

                    val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
                        addNextIntentWithParentStack(deepLink)
                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    }
                    pendingIntent.send()


//                    private fun getCallPendingIntent(
//                        title: String,
//                        sender: String,
//                        recipient: String,
//                        textMessage: String,
//                        context: Context
//                    ): PendingIntent {
//                        val typeEvent = Constants.OUTGOING_CALL_EVENT
//
//                        //"chatalyze://call_screen/{${RECIPIENT_NAME}}/{${RECIPIENT_PHONE}}/{${SENDER_PHONE}}"
////        val deepLink = Uri.parse("chatalyze://call_screen/{${"RECIPIENT_NAME"}}/{${recipient}}/{${sender}}\"")
//                        val deepLink = Uri.parse("chatalyze://call_screen/{${"RECIPIENT_NAME"}}/{${recipient}}/{${sender}}/{${typeEvent}}")
//
//                        val taskDetailIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            deepLink,
//                            context,
//                            MainActivity::class.java
//                        )
//
//                        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
//                            addNextIntentWithParentStack(taskDetailIntent)
//                            getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)
//                        }
//                        return pendingIntent
//                    }








//                    Intent(Intent.ACTION_VIEW, uri).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        context.packageManager?.let { packageManager ->
//                            if (this.resolveActivity(packageManager) != null) {
//                                context.startActivity(this)
//                            }
//                        }
//                    }

//                    val callDomofonActivityIntent = Intent(context, CallDomofonActivity::class.java)
//                    callDomofonActivityIntent.putExtra("address", intent.getStringExtra("address"))
//                    callDomofonActivityIntent.putExtra("imageUrl", intent.getStringExtra("imageUrl"))
//                    callDomofonActivityIntent.putExtra("videoUrl", intent.getStringExtra("videoUrl"))
//                    callDomofonActivityIntent.putExtra("channelID", intent.getStringExtra("channelID")?.toInt())
//                    callDomofonActivityIntent.putExtra("uuid", intent.getStringExtra("uuid"))
//
//                    callDomofonActivityIntent.flags =
//                            // что активность должна быть запущена в новой задаче (также известной как стек активности). Если задача уже существует, новая задача будет создана для активности.
//                        Intent.FLAG_ACTIVITY_NEW_TASK
////                            // все задания в задаче (стеке активности) должны быть удалены перед запуском новой активности. Таким образом, все другие активности в стеке будут закрыты.
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK
////                            //  если активность уже находится в стеке активности, то все активности поверх нее будут удалены, и она будет приведена в верхнюю часть стека.
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP
////                            // если задача (стек активности) будет сброшена системой, все активности в задаче будут удалены.
//                    //       Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
////                            // что если активность уже запущена в задаче (стеке активности), она будет перезапущена с ее изначальным состоянием. В противном случае будет создана новая задача для активности.
//                    //                  Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//                    context.startActivity(callDomofonActivityIntent)
                }
                "click_notification_missed_call" -> {
                    Log.d("4444", " сработал click_notification_missed_call")
                }

                "missed_call_notification_swipe" -> {
//                    val ringtoneIntent = Intent(context, RingtoneService::class.java)
//                    ringtoneIntent.action = RingtoneService.ACTION_STOP_RINGTONE
//                    context?.startService(ringtoneIntent)
//
//                    val channelId = intent.getStringExtra("channelID")
//                    Log.d("4444", " missed_call_notification_swipe channelId=" + channelId)
                    // missed_call_notification_swipe channelId=null
//                    context?.let { ctx ->
//                        deleteNotification(context = ctx, channelId = channelId?.toIntOrNull())
//                    }
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