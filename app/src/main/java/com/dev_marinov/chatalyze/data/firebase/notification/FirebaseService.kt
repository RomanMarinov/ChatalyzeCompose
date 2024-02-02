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

      //  Log.d("4444", " onMessageReceived message=" + message.data)
        //onMessageReceived message={sender=9303454564, topic=, recipient=5551234567, firebaseToken=cf2jBrO0TbmWNhVkaQX7vc:APA91bE2AbQBOzpQJpFW2TIastWXyjmTWjS6zMMrADNhy5hIXHt2bXlT62V_LCb-mraeLI_LFTBomJ7rvzdrQcY4rnz1aJKZ3--FVTnFR5Dkj0Jz3ut38aJ_0kinzlMxS8bO-1V7AfK7}

        if (message.data.isNotEmpty()) {
            val title = message.data["title"]
            val senderPhone = message.data["senderPhone"]
            val recipientPhone = message.data["recipientPhone"]
            val textMessage = message.data["text_message"]
            val typeFirebaseCommand = message.data["typeFirebaseCommand"]


            Log.d("4444", " onMessageReceived senderPhone=" + senderPhone
                    + " recipientPhone=" + recipientPhone
                    + " typeFirebaseCommand=" + typeFirebaseCommand)

            when(typeFirebaseCommand) {
                Constants.TYPE_FIREBASE_MESSAGE_MESSAGE -> {
                    IfLetHelper.execute(senderPhone, recipientPhone) { remoteMessageDataList ->
                        pushNotificationManager.showNotificationMessage(
                            senderPhone = remoteMessageDataList[0],
                            recipientPhone = remoteMessageDataList[1],
                            textMessage = "remoteMessageDataList[3]"
                        )
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