package com.dev_marinov.chatalyze.data.firebase.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.data.firebase.BroadcastReceiverNotification
import com.dev_marinov.chatalyze.data.firebase.RingtoneService
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.PushNotificationManager
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.MainScreensActivity
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PushNotificationManagerImpl @Inject constructor(
    private val context: Context,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val roomRepository: RoomRepository,
) :
    PushNotificationManager {

    val scope = CoroutineScope(Dispatchers.IO)

    val CHANNEL_ID = "0"
    val CHANNEL_NAME = "channel_name"
    val NOTIF_ID = 1

    override fun showNotificationMessage(
        senderPhone: String,
        recipientPhone: String,
        textMessage: String,
    ) {

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            var sessionState = ""
            val result: Deferred<String> = async {
                preferencesDataStoreRepository.isSessionState.first()
            }
            sessionState = result.await()

            val contact = roomRepository.contactBySenderPhone(sender = senderPhone)
            withContext(Dispatchers.Main) {
                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    try {
                        unlockScreen()
                        createNotificationChannel()

                        val deleteIntent =
                            Intent(context, BroadcastReceiverNotification::class.java)
                        deleteIntent.action = "message_notification_swipe"
                        val pendingIntent = PendingIntent.getBroadcast(
                            context, 0, deleteIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )

                        val notification =
                            NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_splash_screen)
                                //.setColor(context.resources.getColor(R.color.bazanet_red_color_anim))
                                //.setLargeIcon(Picasso.get().load(imageUrl).get())
                                .setContentTitle("Message from " + contact.name?.ifEmpty { contact.phoneNumber }) // Заголовок
                                .setContentText(textMessage)
                                .setDeleteIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVibrate(longArrayOf(100, 1000, 200, 340))
                                .setAutoCancel(true) // удаляется после клика
                                .setTicker("Notific")
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) // звуковой файл по умолчанию для уведомления.
                                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                                .setDefaults(Notification.DEFAULT_LIGHTS) // включает мигание светодиода уведомления
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setContentIntent(
                                    getMessagePendingIntent2(
                                        name = contact.name?.ifEmpty { contact.phoneNumber },
                                        sender = senderPhone,
                                        recipient = recipientPhone,
                                        context = context,
                                        sessionState = sessionState
                                    )
                                )
                                .setOngoing(false) // false - уведомление не является постоянным, то есть может быть удалено пользователем.

                        // Отображаем уведомление
                        with(NotificationManagerCompat.from(context)) {
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                //return
                            }
                            notify(NOTIF_ID, notification.build())
                        }
                    } catch (e: Exception) {
                        Log.d("4444", " try catch notification e=" + e)
                    }

                }
            }
            //   }
        }
    }

    override fun showNotificationCall(senderPhone: String, recipientPhone: String) {

        val hasOverlayPermission = Settings.canDrawOverlays(context)

        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isScreenLocked =
            keyguardManager.isDeviceLocked // позволяет получить информацию о состоянии блокировки экрана

        if (isScreenLocked) { // экран погашен
            if (hasOverlayPermission) {
                showPreviewScreen(senderPhone = senderPhone, recipientPhone = recipientPhone)
            } else {
                showPushCall(
                    senderPhone = senderPhone,
                    recipientPhone = recipientPhone,
                    context = context
                )
            }
        } else { // экран включен
            var stateTemp = ""
            val res = preferencesDataStoreRepository.isTheLifecycleEventNow
            runBlocking {
                stateTemp = res.first()
            }
            when (stateTemp) {
                Constants.EVENT_ON_START -> {
                    showPreviewScreen(senderPhone = senderPhone, recipientPhone = recipientPhone)
                }

                Constants.EVENT_ON_STOP -> {
                    if (hasOverlayPermission) {
                        showPreviewScreen(
                            senderPhone = senderPhone,
                            recipientPhone = recipientPhone
                        )
                    } else {
                        showPushCall(
                            senderPhone = senderPhone,
                            recipientPhone = recipientPhone,
                            context = context
                        )
                    }
                }

                Constants.EVENT_ON_DESTROY -> {
                    if (hasOverlayPermission) {
                        showPreviewScreen(
                            senderPhone = senderPhone,
                            recipientPhone = recipientPhone
                        )
                    } else {
                        showPushCall(
                            senderPhone = senderPhone,
                            recipientPhone = recipientPhone,
                            context = context
                        )
                    }
                }
            }
        }
    }

    override fun giveStateReadyStream(
        senderPhone: String,
        recipientPhone: String,
        typeFirebaseCommand: String,
    ) {
        scope.launch {
            roomRepository.saveStateReadyStream(
                senderPhone = senderPhone,
                recipientPhone = recipientPhone,
                typeFirebaseCommand = typeFirebaseCommand
            )
        }
    }

    private fun showPreviewScreen(
        senderPhone: String,
        recipientPhone: String,
    ) {
        unlockScreen()
        ringtoneStart()
        saveHideNavigationBar(hide = true)

        val intent = Intent(context, BroadcastReceiverNotification::class.java)
        intent.action = "full_screen"
        //  intent.putExtra("title", title)
        intent.putExtra("senderPhone", senderPhone)
        intent.putExtra("recipientPhone", recipientPhone)
        // intent.putExtra("textMessage", textMessage)
        intent.putExtra("channelID", CHANNEL_ID)
        context.sendBroadcast(intent)
    }

    private fun unlockScreen() {
        val powerManager =
            context.getSystemService(FirebaseMessagingService.POWER_SERVICE) as PowerManager
        if (!powerManager.isInteractive) { // if screen is not already on, turn it on (get wake_lock)
            @SuppressLint("InvalidWakeLockTag") val wl = powerManager.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "id:wakeupscreen"
            )
            wl.acquire(1000L)
        }
    }

    private fun saveHideNavigationBar(hide: Boolean) {
        scope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = hide
            )
        }
    }

    private fun showPushCall(
        senderPhone: String,
        recipientPhone: String,
        context: Context,
    ) {

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val contact = roomRepository.contactBySenderPhone(sender = senderPhone)

            withContext(Dispatchers.Main) {
                // проверяю есть ли разрешения для уведомлений (true / false)
                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    try {
                        unlockScreen()
                        ringtoneStart()
                        createNotificationChannel()

                        val deleteIntent =
                            Intent(context, BroadcastReceiverNotification::class.java)
                        deleteIntent.action = "call_notification_swipe"
                        val pendingIntent = PendingIntent.getBroadcast(
                            context, 0, deleteIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        ) // вместо PendingIntent.FLAG_IMMUTABLE был 0

                        val notification =
                            NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                //.setColor(context.resources.getColor(R.color.bazanet_red_color_anim))
                                //.setLargeIcon(Picasso.get().load(imageUrl).get())
                                //.setContentText(address) // Основной текст
                                .setContentTitle("Входящий звонок " + contact.name?.ifEmpty { contact.phoneNumber }) // Заголовок
                                .setDeleteIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH) // Приоритет уведомления
                                .setVibrate(longArrayOf(100, 1000, 200, 340))
                                .setAutoCancel(true) // удаляется после клика
                                .setTicker("Notific")
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) // звуковой файл по умолчанию для уведомления.
                                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                                .setDefaults(Notification.DEFAULT_LIGHTS) // включает мигание светодиода уведомления
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setContentIntent(
                                    getCallPendingIntent(
                                        name = contact.name?.ifEmpty { contact.phoneNumber },
                                        sender = senderPhone,
                                        recipient = recipientPhone,
                                        context = context
                                    )
                                )
                                .setOngoing(false) // false - уведомление не является постоянным, то есть может быть удалено пользователем.

                        // Отображаем уведомление
                        with(NotificationManagerCompat.from(context)) {
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {

                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                //return
                            }
                            notify(NOTIF_ID, notification.build())
                        }

                    } catch (e: Exception) {
                        Log.d("4444", " try catch notification e=" + e)
                    }

                } else {
                    val message = "Allow notifications for this application"
                    showToastPermission(toastMessage = message)
                }
            }
        }
    }

    private fun getMessagePendingIntent2(
        name: String?,
        sender: String,
        recipient: String,
        context: Context,
        sessionState: String,
    ): PendingIntent {
        return if (sessionState == Constants.SESSION_SUCCESS) {
            Log.d("4444", " зашло в первую")
            val deepLink =
                Uri.parse("scheme_chatalyze://chat_screen/{$name}/{$sender}/{$recipient}")
            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                deepLink,
//          context,
//          MainScreensActivity::class.java
            )

            val resultPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
            resultPendingIntent
        } else {
            val componentName = ComponentName(context, MainScreensActivity::class.java)
            val activityInfo =
                context.packageManager.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES)
            activityInfo.documentLaunchMode = ActivityInfo.DOCUMENT_LAUNCH_NONE

            val deepLink = Uri.parse("scheme_chatalyze://chat_screen/{$name}/{$sender}/{$recipient}")

            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                deepLink,
//          context,
//          MainScreensActivity::class.java
            )
            val stackBuilder = TaskStackBuilder.create(context.applicationContext)
            stackBuilder.addNextIntentWithParentStack(deepLinkIntent)
            deepLinkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private fun getCallPendingIntent(
        name: String?,
        sender: String,
        recipient: String,
        context: Context,
    ): PendingIntent {
        val typeEvent = Constants.INCOMING_CALL_EVENT

        val deepLink =
            Uri.parse("scheme_chatalyze://call_screen/{${name}}/{${recipient}}/{${sender}}/{${typeEvent}}")

        val taskDetailIntent = Intent(
            Intent.ACTION_VIEW,
            deepLink,
            context,
            MainScreensActivity::class.java
        )

        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(taskDetailIntent)
            getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)
        }
        return pendingIntent
    }

    private fun ringtoneStart() {
        try {
            val intent = Intent(context, RingtoneService::class.java)
            intent.action = RingtoneService.ACTION_PLAY_RINGTONE
            context.startService(intent)
        } catch (e: Exception) {
            Log.d("4444", " try catch Ошибка воспроизведения звука звонка: ", e)
        }
    }

    private fun ringtoneStop() {
        try {
            val intent = Intent(context, RingtoneService::class.java)
            intent.action = RingtoneService.ACTION_STOP_RINGTONE
            context.startService(intent)
        } catch (e: Exception) {
            Log.d("4444", " try catch Ошибка остановки звука звонка: ", e)
        }
    }

    private fun createNotificationChannel() {
        val importance = android.app.NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.lightColor = Color.BLUE
        channel.enableLights(true)
        channel.vibrationPattern =
            longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000) // 10сек

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    private fun showToastPermission(toastMessage: String) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val toast = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG)
            val toastView = toast.view?.findViewById<TextView>(android.R.id.message)
            toastView?.gravity = Gravity.CENTER
            toast.setGravity(Gravity.TOP, 0, 20) // Установить гравитацию для показа Toast наверху
            toast.show()
            delay(2500L)
            toast.show()
        }
    }
}