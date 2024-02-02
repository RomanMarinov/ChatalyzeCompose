package com.dev_marinov.chatalyze.domain.repository

interface PushNotificationManager {
    fun showNotificationCall(senderPhone: String, recipientPhone: String, )
    fun showNotificationMessage(senderPhone: String, recipientPhone: String, textMessage: String)
    fun giveStateReadyStream(senderPhone: String, recipientPhone: String, typeFirebaseCommand: String)
}