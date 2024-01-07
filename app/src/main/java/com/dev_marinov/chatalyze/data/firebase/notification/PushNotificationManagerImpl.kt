package com.dev_marinov.chatalyze.data.firebase.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
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
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.domain.repository.PushNotificationManager
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.MainScreensActivity
import com.dev_marinov.chatalyze.presentation.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    val CHANNEL_ID = "0"
    val CHANNEL_NAME = "channel_name"
    val NOTIF_ID = 1
    val FIRST_ACTION = "first_action"
    lateinit var broadcastReceiver: BroadcastReceiverNotification
    val REQUEST_CODE_NEW = 333

    override fun showNotificationCall(
        title: String,
        sender: String,
        recipient: String,
        textMessage: String,
    ) {

        val coroutineScope = CoroutineScope(context = Dispatchers.IO)
        coroutineScope.launch {
            ringtoneStart()

            showPreviewScreen(
                title = title,
                sender = sender,
                recipient = recipient,
                textMessage = textMessage
            )
//            showPushCall(
//                title = title,
//                sender = sender,
//                recipient = recipient,
//                textMessage = textMessage,
//                context = context
//            )
//

//
//            // написать условие если нет разрешения показывать по верх экрана
//            // то отрисовать пуш с сообщением Звонок в домофон Включить показ на экране смартфона
//
//            // есть ли у приложения разрешение на отображение наложений поверх других приложений
//            val hasOverlayPermission = Settings.canDrawOverlays(context)
//
//            val keyguardManager =
//                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//            val isScreenLocked =
//                keyguardManager.isDeviceLocked // позволяет получить информацию о состоянии блокировки экрана
//            Log.d(
//                "4444",
//                " BroadcastReceiver Notification hasOverlayPermission=" + hasOverlayPermission + " isScreenLocked=" + isScreenLocked
//            )
//
//            // на 12 андр рабочий
//            // не включается экрран при звонке
//            // если нет разрешения и есть пароль и экран выключен  (НЕ ВАЖНО ЕСТЬ ПАРОЛЬ)
//            // BroadcastReceiver Notification hasOverlayPermission=false isScreenLocked=true
//            // если нет разрешения и нет пароля и экран включен  (НЕ ВАЖНО ЕСТЬ ПАРОЛЬ)
//            // BroadcastReceiver Notification hasOverlayPermission=false isScreenLocked=false
//
//
//            if (isScreenLocked) { // экран погашен
//                if (hasOverlayPermission) {
//                    showPreviewScreen(title = title, sender = sender, recipient = recipient, textMessage = textMessage)
//                } else {
//                    // отрисовать пуш
//                    showPushCall(title = title, sender = sender, recipient = recipient, textMessage = textMessage, context = context)
//                }
//            } else { // экран включен
//                Log.d(
//                    "4444",
//                    " 222 BroadcastReceiver Notification hasOverlayPermission=" + hasOverlayPermission + " isScreenLocked=" + isScreenLocked
//                )
//                // НО НАДО УЧИТЫВАТЬ НАХОДУЖЬ ЛИ Я СЕЙЧАС В ПРИЛОЖЕНИИ
//
//                // проблема в том что если я не нахожучь в приле то просто играет мелодия
//                // и внутри появляется экран (ну и он покажется как тогда я зайду)
//
//                // т.е по идее надо запускать приложение тут
//
//                // решение просто запретить в full_screen под hasOverlayPermission показывать calldomofonActivity
//
//                // а если нахожусь в приле то будет экран
////                                       showCallDomofonActivity(address = address, imageUrl = imageUrl, videoUrl = videoUrl, uuid = uuid)
//
//
////                // все круто раотает
////                надо написать экран со свапами
////                и дипликанми разрулить
//
//                var stateTemp = ""
//                val res = preferencesDataStoreRepository.isTheLifecycleEventNow
//                runBlocking {
//                    stateTemp = res.first()
//                }
//                Log.d("4444", " Constants.LifeCycleState.stateTemp=" + stateTemp)
//                when (stateTemp) {
//                    Constants.EVENT_ON_START -> {
//                        Log.d("4444", " Constants.LifeCycleState.ON_START")
//                        showPreviewScreen(title = title, sender = sender, recipient = recipient, textMessage = textMessage)
//                    }
//
//                    Constants.EVENT_ON_STOP -> {
//                        Log.d("4444", " Constants.LifeCycleState.ON_STOP")
//                        if (hasOverlayPermission) {
//                            showPreviewScreen(title = title, sender = sender, recipient = recipient, textMessage = textMessage)
//                        } else {
//                            showPushCall(title = title, sender = sender, recipient = recipient, textMessage = textMessage, context = context)
//                        }
//                    }
//
//                    Constants.EVENT_ON_DESTROY -> {
//                        Log.d("4444", " Constants.LifeCycleState.ON_DESTROY")
//                        if (hasOverlayPermission) {
//                            showPreviewScreen(title = title, sender = sender, recipient = recipient, textMessage = textMessage)
//                        } else {
//                            showPushCall(title = title, sender = sender, recipient = recipient, textMessage = textMessage, context = context)
//                        }
//                    }
//                }
//            }
        }
    }

    private fun showPreviewScreen(
        title: String,
        sender: String,
        recipient: String,
        textMessage: String,
    ) {
        Log.d("4444", " выполнился showPreviewScreen")
        try {
            val intent = Intent(context, BroadcastReceiverNotification::class.java)
            intent.action = "full_screen"
            //  intent.putExtra("title", title)
            intent.putExtra("sender", sender)
            intent.putExtra("recipient", recipient)
            intent.putExtra("textMessage", textMessage)
            intent.putExtra("channelID", CHANNEL_ID)
            context.sendBroadcast(intent)
            //  LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        } catch (e: Exception) {
            Log.d("4444", " try catch showPreviewScreen e=" + e)
        }
    }

    private fun showPushCall(
        title: String,
        sender: String,
        recipient: String,
        textMessage: String,
        context: Context,
    ) {
        Log.d("4444", " showPushCall")

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            roomRepository.contactBySenderPhone(sender = sender)
                .collect { contact ->
                    withContext(Dispatchers.Main) {
                        // проверяю есть ли разрешения для уведомлений (true / false)
                        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                            try {
                                createNotificationChannel()

                                val deleteIntent =
                                    Intent(context, BroadcastReceiverNotification::class.java)
                                deleteIntent.action = "call_notification_swipe"
                                val pendingIntent = PendingIntent.getBroadcast(
                                    context, 0, deleteIntent,
                                    PendingIntent.FLAG_IMMUTABLE
                                ) // вместо PendingIntent.FLAG_IMMUTABLE был 0

                                // https://api.baza.net/domofon/preview/0a2a0820-6774-48ea-80bb-a0fd5d04efe0?ts=1670592955&token=YjZhODY2OWJiZTE3NGNhN2Q1NTQ4MjRmZjM2NzgyZmFiNmEzZjE1OC4xNjcxMTk3NzU1
                                // val icon = Picasso.get().load(imageUrl).placeholder(R.drawable.img_placeholder_camera_dialog).get()

                                val notification =
                                    NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                        //.setColor(context.resources.getColor(R.color.bazanet_red_color_anim))
                                        //.setLargeIcon(Picasso.get().load(imageUrl).get())
                                        .setContentTitle("Входящий звонок " + contact.name.ifEmpty { contact.phoneNumber }) // Заголовок
                                        //.setContentText(address) // Основной текст
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
                                                name = contact.name,
                                                sender = sender,
                                                recipient = recipient,
                                                textMessage = textMessage,
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

                                ringtoneStart()
                            } catch (e: Exception) {
                                Log.d("4444", " try catch notification e=" + e)
                            }

                        } else {
//            val message = context.getString(R.string.notification_allow)
//            showToastPermission(toastMessage = message)
                        }
                    }
                }
        }
    }

    private fun getCallPendingIntent(
        name: String,
        sender: String,
        recipient: String,
        textMessage: String,
        context: Context,
    ): PendingIntent {
        val typeEvent = Constants.INCOMING_CALL_EVENT

        //"chatalyze://call_screen/{${RECIPIENT_NAME}}/{${RECIPIENT_PHONE}}/{${SENDER_PHONE}}"
//        val deepLink = Uri.parse("chatalyze://call_screen/{${"RECIPIENT_NAME"}}/{${recipient}}/{${sender}}\"")
        val deepLink =
            Uri.parse("scheme_chatalyze://call_screen/{${name}}/{${recipient}}/{${sender}}/{${typeEvent}}")

        //val uri = "www.schemechatalyze.com/${"profile_screen"}".toUri()
//        val uri = "www.schemechatalyze.com/${"profile_screen"}".toUri()
// ЭТО ПУШ

        val taskDetailIntent = Intent(
            Intent.ACTION_VIEW,
//            Uri.parse("profile_screen"),
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

    private fun getManageOverlayPendingIntent(): PendingIntent {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

//    override fun showNotificationMissedCall(
//        address: String,
//        imageUrl: String,
//        uuid: String,
//        content: String,
//        title: String,
//        token: String?,
//        accessToken: String,
//    ) {
//        isCollectEnabledForMissedCall = true
//        val coroutineScope = CoroutineScope(context = Dispatchers.IO)
//        coroutineScope.launch {
//            domofonRepository.getDomofonCallSelectable.collect {
//                it?.let {
//                    Log.d(
//                        "4444",
//                        " showNotificationCall isCollectEnabled =" + isCollectEnabledForCall
//                    )
//                    if (isCollectEnabledForMissedCall) {
//                        disableCollectMissedCall()
//                        for (i in it.indices) {
//                            if (uuid == it[i].deviceID && it[i].isSelected == true) {
//
//                                Log.d(
//                                    "4444",
//                                    " NotificationManagerImpl showNotificationMissedCall address=" + address + " imageUrl=" + imageUrl
//                                )
//                                // проверяю есть ли разрешения для уведомлений (true / false)
//                                if (NotificationManagerCompat.from(context)
//                                        .areNotificationsEnabled()
//                                ) {
//                                    try {
//                                        ringtoneStop()
//
//                                        createNotificationChannel()
//
//                                        val deleteIntent = Intent(
//                                            context,
//                                            BroadcastReceiverNotification::class.java
//                                        )
//                                        deleteIntent.action = "missed_call_notification_swipe"
//                                        // deleteIntent.putExtra("channelID", "111")
//                                        val pendingIntent = PendingIntent.getBroadcast(
//                                            context,
//                                            0,
//                                            deleteIntent,
//                                            PendingIntent.FLAG_IMMUTABLE
//                                        ) // вместо PendingIntent.FLAG_IMMUTABLE был 0
//
//                                        // https://api.baza.net/domofon/preview/0a2a0820-6774-48ea-80bb-a0fd5d04efe0?ts=1670592955&token=YjZhODY2OWJiZTE3NGNhN2Q1NTQ4MjRmZjM2NzgyZmFiNmEzZjE1OC4xNjcxMTk3NzU1
//                                        // val icon = Picasso.get().load(imageUrl).placeholder(R.drawable.img_placeholder_camera_dialog).get()
//
//                                        val notification =
//                                            NotificationCompat.Builder(context, CHANNEL_ID)
//                                                .setSmallIcon(R.drawable.notificationiconblack3)
//                                                .setColor(context.resources.getColor(R.color.bazanet_red_color_anim))
//                                                .setLargeIcon(Picasso.get().load(imageUrl).get())
//                                                .setContentTitle(title) // Заголовок
//                                                .setContentText(content) // Основной текст
//                                                .setDeleteIntent(pendingIntent)
//                                                .setStyle(
//                                                    NotificationCompat.BigTextStyle()
//                                                        .bigText("адрес: $address")
//                                                )
//                                                .setPriority(NotificationCompat.PRIORITY_HIGH) // Приоритет уведомления
//                                                .setVibrate(longArrayOf(100, 1000, 200, 340))
//                                                .setAutoCancel(true) // удаляется после клика
//                                                .setTicker("Notific")
//
//                                                //.addAction(0, "Открыть", getMissedCallPendingIntent(context))
//                                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) // звуковой файл по умолчанию для уведомления.
//                                                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
//                                                .setDefaults(Notification.DEFAULT_LIGHTS) // включает мигание светодиода уведомления
//                                                .setCategory(NotificationCompat.CATEGORY_ALARM)
////                                                .setContentIntent(getMissedCallPendingIntent(context))
////                                                .setContentIntent(getMissedCallPendingIntent(context))
//                                                .setContentIntent(getMissedCallPendingIntent(context))
//                                                .setOngoing(false) // false - уведомление не является постоянным, то есть может быть удалено пользователем.
//
//                                        // Отображаем уведомление
//                                        with(NotificationManagerCompat.from(context)) {
//                                            if (ActivityCompat.checkSelfPermission(
//                                                    context,
//                                                    Manifest.permission.POST_NOTIFICATIONS
//                                                ) != PackageManager.PERMISSION_GRANTED
//                                            ) {
//
//                                                //    ActivityCompat#requestPermissions
//                                                // here to request the missing permissions, and then overriding
//                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                //                                          int[] grantResults)
//                                                // to handle the case where the user grants the permission. See the documentation
//                                                // for ActivityCompat#requestPermissions for more details.
//                                                //return
//                                            }
//                                            notify(NOTIF_ID, notification.build())
//                                        }
//                                    } catch (e: Exception) {
//                                        Log.d(
//                                            "4444",
//                                            " try catch showNotificationMissedCall e=" + e
//                                        )
//                                    }
//                                } else {
//                                    val message = context.getString(R.string.notification_allow)
//                                    //                                  showToastPermission(toastMessage = message)
//
//                                    ShowToastHelper.createToast(
//                                        message = message,
//                                        context = context
//                                    )
//                                }
//                            }
//                        }
//                        killCallDomofonActivity()
//                    }
//                }
//            }
//        }
//    }

    private fun killCallDomofonActivity() {

        val intent = Intent(context, BroadcastReceiverNotification::class.java)
        intent.action = "kill_screen"
        context.sendBroadcast(intent)
    }

    private fun ringtoneStart() {
        try {
            val intent = Intent(context, RingtoneService::class.java)
            intent.action = RingtoneService.ACTION_PLAY_RINGTONE
            context.startService(intent)
            //8999 c.startForegroundService(intent2);
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

//    private fun getMissedCallPendingIntent(context: Context): PendingIntent {
//        Log.d("4444", " типа проверил accessToken")
//        return NavDeepLinkBuilder(context)
//            .setComponentName(MainActivity::class.java)
//            .setGraph(R.navigation.nav_graph)
//            .setDestination(R.id.historyCallFragment)
//            .createPendingIntent()
//    }

//    private fun getMissedCallPendingIntent(context: Context): PendingIntent {
//        val intent = Intent(context, BroadcastReceiverNotification::class.java)
//        intent.action = "click_notification_missed_call"
//        context.sendBroadcast(intent)
//        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//    }

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

    // оригинальный метод для нотификации пока сохранил
//    override fun showNotificationCall(
//        address: String,
//        imageUrl: String,
//        uuid: String,
//        content: String,
//        title: String,
//        videoUrl: String
//    ) {
//        val packageName = context.packageName
//        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
//        startActivityForResult(context, intent, REQUEST_CODE_NEW, null)

///////////////////////////////////////////////////////////////////////////////////////
//        isCollectEnabledForCall = true
//        val coroutineScope = CoroutineScope(context = Dispatchers.IO)
//        coroutineScope.launch {
//            domofonCamerasRepository.getDomofonCallSelectable.collect {
//        Log.d("4444", " showNotificationCall isCollectEnabled =" + isCollectEnabledForCall)
//                if (isCollectEnabledForCall) {
//                    disableCollectCall()
//                    for (i in it.indices) {
//                        if (uuid == it[i].deviceID && it[i].isSelected == true) {
//
////                             Создаём уведомление
////        broadcastReceiver = MyBroadcastReceiver()
////        val intentFilter = IntentFilter(FIRST_ACTION)
////        context.registerReceiver(broadcastReceiver, intentFilter)
//
//
//                            // проверяю есть ли разрешения для уведомлений (true / false)
//                            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
//                                try {
//                                    createNotificationChannel()
//
//                                    val deleteIntent = Intent(context, BroadcastReceiverNotification::class.java)
//                                    deleteIntent.action = "call_notification_swipe"
//                                    val pendingIntent = PendingIntent.getBroadcast(
//                                        context, 0, deleteIntent,
//                                        PendingIntent.FLAG_IMMUTABLE
//                                    ) // вместо PendingIntent.FLAG_IMMUTABLE был 0
//
//                                    // https://api.baza.net/domofon/preview/0a2a0820-6774-48ea-80bb-a0fd5d04efe0?ts=1670592955&token=YjZhODY2OWJiZTE3NGNhN2Q1NTQ4MjRmZjM2NzgyZmFiNmEzZjE1OC4xNjcxMTk3NzU1
//                                    // val icon = Picasso.get().load(imageUrl).placeholder(R.drawable.img_placeholder_camera_dialog).get()
//
//                                    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                                            .setSmallIcon(R.drawable.notificationiconblack2)
//                                            //.setLargeIcon(Picasso.get().load(imageUrl).get())
//                                            .setContentTitle(title) // Заголовок
//                                            .setContentText(content) // Основной текст
//                                            .setDeleteIntent(pendingIntent)
//                                        .addAction(0, "\uD83D\uDD34 Отклонить", getCallPendingIntentForIconTray(action = "drop",  uuid = uuid))
//                                        .addAction(0, "\uD83D\uDFE2 Открыть дверь", getCallPendingIntentForIconTray(action = "open",  uuid = uuid))
////                                            .addAction(0, "\uD83D\uDD34 Отклонить", getCallPendingIntent(context, "drop", address = address, imageUrl = imageUrl, videoUrl = videoUrl, uuid = uuid))
////                                            .addAction(0, "\uD83D\uDFE2 Открыть дверь", getCallPendingIntent(context, "open", address = address, imageUrl = imageUrl, videoUrl = videoUrl, uuid = uuid))
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH) // Приоритет уведомления
//                                            .setVibrate(longArrayOf(100, 1000, 200, 340))
//                                            .setAutoCancel(true) // удаляется после клика
//                                            .setTicker("Notific")
//                                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) // звуковой файл по умолчанию для уведомления.
//                                            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
//                                            .setDefaults(Notification.DEFAULT_LIGHTS) // включает мигание светодиода уведомления
//                                            .setCategory(NotificationCompat.CATEGORY_ALARM)
//                                            .setContentIntent(getCallPendingIntent(context, "get", address = address, imageUrl = imageUrl, videoUrl = videoUrl, uuid = uuid))
//                                            .setOngoing(false) // false - уведомление не является постоянным, то есть может быть удалено пользователем.
//
//                                    // Отображаем уведомление
//                                    with(NotificationManagerCompat.from(context)) {
//                                        if (ActivityCompat.checkSelfPermission(
//                                                context,
//                                                Manifest.permission.POST_NOTIFICATIONS
//                                            ) != PackageManager.PERMISSION_GRANTED
//                                        ) {
//
//                                            //    ActivityCompat#requestPermissions
//                                            // here to request the missing permissions, and then overriding
//                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                            //                                          int[] grantResults)
//                                            // to handle the case where the user grants the permission. See the documentation
//                                            // for ActivityCompat#requestPermissions for more details.
//                                            //return
//                                        }
//                                        notify(NOTIF_ID, notification.build())
//                                    }
//
//                                    ringtoneStart()
//                                } catch (e: Exception) {
//                                    Log.d("4444", " try catch notification e=" + e)
//                                }
//
//                            } else {
//                                val message = context.getString(R.string.notification_allow)
//                                showToastPermission(toastMessage = message)
//                            }
//                        }
//                    }
//                }
//            }
//        }
    ///////////////////////////////////////////////////////////////////////////////////////////
//    }

//    private fun getCallPendingIntent(
//        context: Context,
//        action: String,
//        address: String,
//        imageUrl: String,
//        videoUrl: String,
//        uuid: String
//    ): PendingIntent {
//
//        val intent = Intent(context, CallDomofonActivity::class.java).setAction(action)
//        // Настраиваем флаги для нового объекта Intent
//        intent.putExtra("actionNew", action)
//        // Передаем параметры action, imageUrl и videoUrl в Intent
//        intent.putExtra("addressNew", address)
//        intent.putExtra("imageUrlNew", imageUrl)
//        intent.putExtra("videoUrlNew", videoUrl)
//        intent.putExtra("channel_idNew", CHANNEL_ID)
//        intent.putExtra("uuid", uuid)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        // Создаем объект PendingIntent
//        // Функция getActivity() возвращает объект PendingIntent
//        // Если такой объект уже существует, он будет использован
//        // FLAG_IMMUTABLE - если установлен этот флаг, PendingIntent будет иметь "немутабельный" тип и не может быть модифицирован
//
//        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
}