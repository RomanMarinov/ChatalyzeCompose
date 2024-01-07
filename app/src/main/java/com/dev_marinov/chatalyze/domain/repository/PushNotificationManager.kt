package com.dev_marinov.chatalyze.domain.repository

interface PushNotificationManager {
    fun showNotificationCall(
        title: String,
        sender: String,
        recipient: String,
        textMessage: String
    )
}