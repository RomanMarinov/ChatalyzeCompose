package com.dev_marinov.chatalyze.data.firebase.model

data class StateReadyStream(
    val senderPhone: String,
    val recipientPhone: String,
    val typeFirebaseCommand: String,
)
