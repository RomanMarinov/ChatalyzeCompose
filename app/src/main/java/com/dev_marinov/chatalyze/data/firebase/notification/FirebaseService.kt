package com.dev_marinov.chatalyze.data.firebase.notification

import android.util.Log
import com.dev_marinov.chatalyze.domain.repository.PushNotificationManager
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.IfLetHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : FirebaseMessagingService() {

    @Inject
    lateinit var pushNotificationManager: PushNotificationManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("4444", " onNewToken=" + token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.data.isNotEmpty()) {
            val title = message.data["title"]
            val senderPhone = message.data["senderPhone"]
            val recipientPhone = message.data["recipientPhone"]
            val textMessage = message.data["textMessage"]
            val typeFirebaseCommand = message.data["typeFirebaseCommand"]

            when(typeFirebaseCommand) {
                Constants.TYPE_FIREBASE_MESSAGE_MESSAGE -> {
                    try {
                        IfLetHelper.execute(senderPhone, recipientPhone, textMessage) { remoteMessageDataList ->
                            pushNotificationManager.showNotificationMessage(
                                senderPhone = remoteMessageDataList[0],
                                recipientPhone = remoteMessageDataList[1],
                                textMessage = remoteMessageDataList[2]
                            )
                        }
                    } catch (e: Exception) {
                        Log.d("4444", " try catch TYPE_FIREBASE_MESSAGE_MESSAGE e=" + e)
                    }
                }
                Constants.TYPE_FIREBASE_MESSAGE_CALL -> {
                    IfLetHelper.execute(senderPhone, recipientPhone) { remoteMessageDataList ->
                        pushNotificationManager.showNotificationCall(
                            senderPhone = remoteMessageDataList[0],
                            recipientPhone = remoteMessageDataList[1]
                        )
                    }
                }
                Constants.TYPE_FIREBASE_MESSAGE_READY_STREAM -> {
                    IfLetHelper.execute(senderPhone, recipientPhone, typeFirebaseCommand) { remoteMessageDataList ->
                        pushNotificationManager.giveStateReadyStream(
                            senderPhone = remoteMessageDataList[0],
                            recipientPhone = remoteMessageDataList[1],
                            typeFirebaseCommand = remoteMessageDataList[2]
                        )
                    }
                }
            }
        }
    }
}